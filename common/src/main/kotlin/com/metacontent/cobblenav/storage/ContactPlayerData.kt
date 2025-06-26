package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.npc.NPCClass
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.entity.npc.NPCBattleActor
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer
import com.metacontent.cobblenav.api.contact.BattleId
import com.metacontent.cobblenav.api.contact.ContactBattleRecord.Companion.getBattleRecord
import com.metacontent.cobblenav.api.contact.ContactType
import com.metacontent.cobblenav.api.contact.PokenavContact
import com.metacontent.cobblenav.api.contact.npc.NPCProfiles
import com.metacontent.cobblenav.api.event.CobblenavEvents
import com.metacontent.cobblenav.api.event.contact.ContactsAdded
import com.metacontent.cobblenav.api.event.contact.ContactsRemoved
import com.metacontent.cobblenav.api.event.contact.ContactsUpdated
import com.metacontent.cobblenav.storage.client.ClientContactPlayerData
import com.metacontent.cobblenav.util.ContactSharingManager
import com.metacontent.cobblenav.util.getContactData
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerPlayer
import java.util.*

data class ContactPlayerData(
    override val uuid: UUID,
    val contacts: HashMap<String, PokenavContact>,
) : InstancedPlayerData {
    companion object {
        val CODEC: Codec<ContactPlayerData> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                PokenavContact.CODEC.listOf().fieldOf("contacts").forGetter { it.contacts.values.toList() },
            ).apply(instance) { uuid, contacts ->
                ContactPlayerData(
                    UUID.fromString(uuid),
                    HashMap(contacts.associateBy(PokenavContact::id)),
                )
            }
        }

        fun executeAndSave(uuid: UUID, action: (ContactPlayerData) -> Boolean): Boolean {
            val data = Cobblemon.playerDataManager.getContactData(uuid)
            if (action(data)) {
                Cobblemon.playerDataManager.saveSingle(data, CobblenavDataStoreTypes.CONTACTS)
                return true
            }
            return false
        }

        fun executeAndSave(player: ServerPlayer, action: (ContactPlayerData) -> Boolean): Boolean {
            return executeAndSave(player.uuid, action)
        }

        internal fun onBattleEnd(event: BattleVictoryEvent) {
            val id = BattleId(event.battle.battleId)
            val playerActors = event.battle.actors.filterIsInstance<PlayerBattleActor>()
            playerActors.forEach { playerActor ->
                val player = playerActor.entity ?: return@forEach
                executeAndSave(player) { data ->
                    val contacts = event.battle.actors.flatMap { actor ->
                        when (actor.type) {
                            ActorType.PLAYER -> actor.getPlayerUUIDs().mapNotNull { uuid ->
                                if (!ContactSharingManager.checkSharing(uuid)) return@mapNotNull null
                                uuid.getPlayer()?.let {
                                    PokenavContact(
                                        id = uuid.toString(),
                                        type = ContactType.PLAYER,
                                        name = it.name.string,
                                        battles = hashMapOf(
                                            id to actor.getBattleRecord(
                                                id,
                                                playerActor,
                                                event.winners
                                            )
                                        )
                                    )
                                }
                            }

                            ActorType.NPC -> {
                                val npcActor = actor as? NPCBattleActor ?: return@flatMap emptyList()
                                val contact = NPCProfiles.get(npcActor.npc.npc.id)?.let {
                                    if (!it.shareContactAfterBattle) return@let null
                                    val npcId =
                                        if (it.commonForAllEntities) it.id.toString() else npcActor.npc.stringUUID
                                    if (data.find(npcId) == null && !it.recordBattles) return@let null
                                    PokenavContact(
                                        id = npcId,
                                        type = ContactType.NPC,
                                        name = it.name ?: npcActor.npc.name.string,
                                        battles = hashMapOf(
                                            id to npcActor.getBattleRecord(
                                                id,
                                                playerActor,
                                                event.winners
                                            )
                                        )
                                    )
                                } ?: return@flatMap emptyList()
                                return@flatMap listOf(contact)
                            }

                            else -> emptyList()
                        }
                    }
                    return@executeAndSave data.updateContacts(contacts)
                }
            }
        }
    }

    private val player: ServerPlayer? by lazy { uuid.getPlayer() }

    fun find(player: ServerPlayer) = find(player.uuid)

    fun find(npcClass: NPCClass) = find(npcClass.id.toString())

    fun find(uuid: UUID) = find(uuid.toString())

    fun find(id: String) = contacts[id]

    fun findByName(name: String) = contacts.values.filter { it.name == name }

    fun updateContacts(contact: PokenavContact): Boolean {
        if (contact.id == uuid.toString()) return false
        val existingContact = find(contact.id)
        val updated = if (existingContact == null) {
            contacts[contact.id] = contact
            CobblenavEvents.CONTACTS_ADDED.post(ContactsAdded(player, listOf(contact)))
            true
        } else if (contact.battles.isNotEmpty()) {
            existingContact.battles.putAll(contact.battles)
            CobblenavEvents.CONTACTS_UPDATED.post(ContactsUpdated(player, listOf(existingContact)))
            true
        } else {
            false
        }

        if (updated) {
            onContactListUpdated()
            return true
        }
        return false
    }

    fun updateContacts(contacts: List<PokenavContact>): Boolean {
        val addedContacts = mutableListOf<PokenavContact>()
        val updatedContacts = mutableListOf<PokenavContact>()
        val updated = contacts.map { contact ->
            if (contact.id == uuid.toString()) return@map false
            val existingContact = find(contact.id)
            return@map if (existingContact == null) {
                this.contacts[contact.id] = contact
                addedContacts.add(contact)
                true
            } else if (contact.battles.isNotEmpty()) {
                existingContact.battles.putAll(contact.battles)
                updatedContacts.add(existingContact)
                true
            } else {
                false
            }
        }.any { it }

        if (updated) {
            if (addedContacts.isNotEmpty()) {
                CobblenavEvents.CONTACTS_ADDED.post(ContactsAdded(player, addedContacts))
            }
            if (updatedContacts.isNotEmpty()) {
                CobblenavEvents.CONTACTS_UPDATED.post(ContactsUpdated(player, updatedContacts))
            }
            onContactListUpdated()
            return true
        }
        return false
    }

    fun removeContact(id: String): Boolean {
        val contact = contacts.remove(id) ?: return false
        CobblenavEvents.CONTACTS_REMOVED.post(ContactsRemoved(player, listOf(contact)))
        onContactListUpdated()
        return true
    }

    fun clearContacts() {
        if (contacts.isEmpty()) return
        val values = contacts.values.toList()
        contacts.clear()
        CobblenavEvents.CONTACTS_REMOVED.post(ContactsRemoved(player, values))
        onContactListUpdated()
    }

    private fun onContactListUpdated() {
        player?.let {
            SetClientPlayerDataPacket(
                type = CobblenavDataStoreTypes.CONTACTS,
                playerData = this.toClientData()
            )
        }
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientContactPlayerData(
            HashMap(contacts.mapValues { it.value.toClientContact() }),
        )
    }
}