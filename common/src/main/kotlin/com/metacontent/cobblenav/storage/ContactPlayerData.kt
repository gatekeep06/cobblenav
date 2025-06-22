package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.npc.NPCClass
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer
import com.metacontent.cobblenav.api.contact.*
import com.metacontent.cobblenav.api.contact.BattleRecord.Companion.toParticipants
import com.metacontent.cobblenav.api.event.CobblenavEvents
import com.metacontent.cobblenav.api.event.contact.ContactsAdded
import com.metacontent.cobblenav.api.event.contact.ContactsRemoved
import com.metacontent.cobblenav.api.event.contact.ContactsUpdated
import com.metacontent.cobblenav.storage.client.ClientContactPlayerData
import com.metacontent.cobblenav.util.getContactData
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerPlayer
import java.util.*

data class ContactPlayerData(
    override val uuid: UUID,
    val contacts: HashMap<String, PokenavContact>,
    val battles: HashMap<BattleId, BattleRecord>
) : InstancedPlayerData {
    companion object {
        val CODEC: Codec<ContactPlayerData> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                PokenavContact.CODEC.listOf().fieldOf("contacts").forGetter { it.contacts.values.toList() },
                BattleRecord.CODEC.listOf().fieldOf("battles").forGetter { it.battles.values.toList() }
            ).apply(instance) { uuid, contacts, battles ->
                ContactPlayerData(
                    UUID.fromString(uuid),
                    HashMap(contacts.associateBy(PokenavContact::id)),
                    HashMap(battles.associateBy(BattleRecord::id))
                )
            }
        }

        fun executeAndSafe(uuid: UUID, action: (ContactPlayerData) -> Boolean) {
            val data = Cobblemon.playerDataManager.getContactData(uuid)
            if (action(data)) {
                Cobblemon.playerDataManager.saveSingle(data, CobblenavDataStoreTypes.CONTACTS)
            }
        }

        fun executeAndSafe(player: ServerPlayer, action: (ContactPlayerData) -> Boolean) {
            executeAndSafe(player.uuid, action)
        }

        internal fun onBattleEnd(event: BattleVictoryEvent) {
            val id = BattleId(event.battle.battleId)
            val winners = event.winners.toParticipants()
            val losers = event.losers.toParticipants()
            val uuids = event.battle.actors.flatMap { actor -> actor.getPlayerUUIDs() }
            val contacts = event.battle.actors.flatMap { actor ->
                when (actor.type) {
                    ActorType.PLAYER -> actor.getPlayerUUIDs().mapNotNull { uuid ->
                        uuid.getPlayer()?.let {
                            PokenavContact(
                                id = uuid.toString(),
                                type = ContactType.PLAYER,
                                name = it.name.string,
                                battles = mutableListOf(id)
                            )
                        }
                    }

                    ActorType.NPC -> emptyList()
                    else -> emptyList()
                }
            }
            uuids.forEach { playerUUID ->
                executeAndSafe(playerUUID) { data ->
                    val battle = BattleRecord(
                        id = id,
                        winners = winners,
                        losers = losers,
                        type = if (winners.keys.contains(playerUUID.toString())) {
                            RecordType.WIN
                        } else {
                            RecordType.LOSS
                        }
                    )
                    data.battles[id] = battle
                    return@executeAndSafe data.updateContacts(contacts)
                }
            }
        }
    }

    private val player: ServerPlayer? by lazy { uuid.getPlayer() }

    fun find(player: ServerPlayer) = find(player.uuid)

    fun find(npcClass: NPCClass) = find(npcClass.id.toString())

    fun find(uuid: UUID) = find(uuid.toString())

    fun find(id: String) = contacts[id]

    fun findByName(name: String) = contacts.values.firstOrNull { it.name == name }

    fun updateContacts(contact: PokenavContact): Boolean {
        if (contact.id == uuid.toString()) return false
        val existingContact = find(contact.id)
        val updated = if (existingContact == null) {
            contacts[contact.id] = contact
            CobblenavEvents.CONTACTS_ADDED.post(ContactsAdded(player, listOf(contact)))
            true
        } else if (contact.battles.isNotEmpty()) {
            existingContact.battles.addAll(contact.battles)
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
                existingContact.battles.addAll(contact.battles)
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
            battles
        )
    }
}