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
import com.metacontent.cobblenav.api.contact.ContactBattleRecord
import com.metacontent.cobblenav.api.contact.ContactBattleRecord.Companion.getBattleRecord
import com.metacontent.cobblenav.api.contact.ContactType
import com.metacontent.cobblenav.api.contact.PokenavContact
import com.metacontent.cobblenav.api.contact.npc.NPCProfiles
import com.metacontent.cobblenav.api.event.CobblenavEvents
import com.metacontent.cobblenav.api.event.contact.ContactsAdded
import com.metacontent.cobblenav.api.event.contact.ContactsRemoved
import com.metacontent.cobblenav.storage.client.ClientContactPlayerData
import com.metacontent.cobblenav.util.ContactSharingManager
import com.metacontent.cobblenav.util.getContactData
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.server.level.ServerPlayer
import java.util.*

data class ContactPlayerData(
    override val uuid: UUID,
    val contacts: HashMap<String, PokenavContact>,
    val battles: HashMap<String, MutableList<ContactBattleRecord>>
) : InstancedPlayerData {
    companion object {
        val CODEC: Codec<ContactPlayerData> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                PokenavContact.CODEC.listOf().fieldOf("contacts").forGetter { it.contacts.values.toList() },
                UnboundedMapCodec(PrimitiveCodec.STRING, ContactBattleRecord.CODEC.listOf())
                    .fieldOf("battles")
                    .forGetter { it.battles }
            ).apply(instance) { uuid, contacts, battles ->
                ContactPlayerData(
                    UUID.fromString(uuid),
                    HashMap(contacts.associateBy(PokenavContact::id)),
                    HashMap(battles)
                )
            }
        }

        fun executeAndSave(uuid: UUID, action: (ContactPlayerData) -> Boolean): Boolean {
            val data = Cobblemon.playerDataManager.getContactData(uuid)
            if (action(data)) {
                Cobblemon.playerDataManager.saveSingle(data, CobblenavDataStoreTypes.CONTACTS)
                data.sync()
                return true
            }
            return false
        }

        fun executeAndSave(player: ServerPlayer, action: (ContactPlayerData) -> Boolean): Boolean {
            return executeAndSave(player.uuid, action)
        }

        internal fun onBattleEnd(event: BattleVictoryEvent) {
            val battleId = BattleId(event.battle.battleId)
            val playerActors = event.battle.actors.filterIsInstance<PlayerBattleActor>()
            playerActors.forEach { contactReceiver ->
                val player = contactReceiver.entity ?: return@forEach
                executeAndSave(player) { data ->
                    val battles = mutableMapOf<String, ContactBattleRecord>()
                    val contacts = event.battle.actors.mapNotNull { actor ->
                        when (actor.type) {
                            ActorType.PLAYER -> {
                                val playerActor = actor as? PlayerBattleActor ?: return@mapNotNull null
                                if (!ContactSharingManager.checkSharing(playerActor.uuid)) return@mapNotNull null
                                battles[playerActor.uuid.toString()] = actor.getBattleRecord(
                                    id = battleId,
                                    contactReceiver = contactReceiver,
                                    winners = event.winners
                                )
                                return@mapNotNull playerActor.entity?.let {
                                    PokenavContact(
                                        id = playerActor.uuid.toString(),
                                        type = ContactType.PLAYER,
                                        name = it.name.string
                                    )
                                }
                            }

                            ActorType.NPC -> {
                                val npcActor = actor as? NPCBattleActor ?: return@mapNotNull null
                                return@mapNotNull NPCProfiles.get(npcActor.npc.npc.id)?.let {
                                    if (!it.postBattleContact.canShare(
                                            playerActor = contactReceiver,
                                            npcActor = npcActor,
                                            winners = event.winners
                                        )
                                    ) return@let null
                                    val id = if (it.commonForAllEntities) it.id.toString() else npcActor.npc.stringUUID
                                    battles[id] = actor.getBattleRecord(
                                        id = battleId,
                                        contactReceiver = contactReceiver,
                                        winners = event.winners
                                    )
                                    return@let it.postBattleContact.provide(
                                        contact = PokenavContact(
                                            id = id,
                                            type = ContactType.NPC,
                                            name = it.name ?: npcActor.npc.name.string
                                        ),
                                        playerActor = contactReceiver,
                                        npcActor = npcActor,
                                        winners = event.winners
                                    )
                                }
                            }

                            else -> null
                        }
                    }
                    battles.forEach { entry ->
                        data.battles.putIfAbsent(entry.key, mutableListOf(entry.value))?.add(entry.value)
                    }
                    return@executeAndSave data.addContacts(contacts) || battles.isNotEmpty()
                }
            }
        }

//        private fun handlePlayerActor(actor: BattleActor): PokenavContact? {
//            val playerActor = actor as? PlayerBattleActor ?: return null
//            if (!ContactSharingManager.checkSharing(playerActor.uuid)) return null
//            return playerActor.entity?.let {
//                PokenavContact(
//                    id = playerActor.uuid.toString(),
//                    type = ContactType.PLAYER,
//                    name = it.name.string
//                )
//            }
//        }
//
//        private fun handleNPCActor(actor: BattleActor): PokenavContact? {
//            val npcActor = actor as? NPCBattleActor ?: return null
//            return NPCProfiles.get(npcActor.npc.npc.id)?.let {
//                if (!it.postBattleContact.canShare()) return@let null
//                PokenavContact(
//                    id = if (it.commonForAllEntities) it.id.toString() else npcActor.npc.stringUUID,
//                    type = ContactType.NPC,
//                    name = it.name ?: npcActor.npc.name.string
//                )
//            }
//        }
    }

    private val player: ServerPlayer? by lazy { uuid.getPlayer() }

    fun find(player: ServerPlayer) = find(player.uuid)

    fun find(npcClass: NPCClass) = find(npcClass.id.toString())

    fun find(uuid: UUID) = find(uuid.toString())

    fun find(id: String) = contacts[id]

    fun findByName(name: String) = contacts.values.filter { it.name == name }

    fun addContact(contact: PokenavContact): Boolean {
        if (contact.id == uuid.toString()) return false
        return (contacts.put(contact.id, contact) == null).also {
            if (it) {
                CobblenavEvents.CONTACTS_ADDED.post(ContactsAdded(player, listOf(contact)))
            }
        }
    }

    fun addContacts(contacts: Iterable<PokenavContact>): Boolean {
        val added = contacts.filter {
            if (it.id == uuid.toString()) {
                false
            } else {
                this.contacts.put(it.id, it) == null
            }
        }
        return added.isNotEmpty().also {
            if (it) {
                CobblenavEvents.CONTACTS_ADDED.post(ContactsAdded(player, added))
            }
        }
    }

    fun summarizeBattles(id: String): String? {
        return battles[id]
            ?.groupBy { it.result }
            ?.map { "${it.key.name}: ${it.value.size}" }
            ?.joinToString()
    }

    fun removeContact(id: String): Boolean {
        val contact = contacts.remove(id) ?: return false
        CobblenavEvents.CONTACTS_REMOVED.post(ContactsRemoved(player, listOf(contact)))
        return true
    }

    fun clearContacts(): Boolean {
        if (contacts.isEmpty()) return false
        val values = contacts.values.toList()
        contacts.clear()
        CobblenavEvents.CONTACTS_REMOVED.post(ContactsRemoved(player, values))
        return true
    }

    private fun sync() {
        player?.let {
            SetClientPlayerDataPacket(
                type = CobblenavDataStoreTypes.CONTACTS,
                playerData = this.toClientData()
            ).sendToPlayer(it)
        }
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientContactPlayerData(
            HashMap(contacts.mapValues { it.value.toClientContact() }),
        )
    }
}