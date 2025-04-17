package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer
import com.metacontent.cobblenav.api.contact.ContactID
import com.metacontent.cobblenav.api.contact.PokenavContact
import com.metacontent.cobblenav.api.event.CobblenavEvents
import com.metacontent.cobblenav.api.event.contact.ContactsAdded
import com.metacontent.cobblenav.api.event.contact.ContactDataCreated
import com.metacontent.cobblenav.api.event.contact.ContactsRemoved
import com.metacontent.cobblenav.storage.client.ClientContactPlayerData
import com.metacontent.cobblenav.util.getContactData
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerPlayer
import java.util.*

data class ContactPlayerData(
    override val uuid: UUID,
    val contacts: MutableMap<ContactID, PokenavContact>
) : InstancedPlayerData {
    companion object {
        val CODEC: Codec<ContactPlayerData> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                PokenavContact.CODEC.listOf().fieldOf("contacts").forGetter { it.contacts.values.toList() }
            ).apply(instance) { uuid, contacts ->
                ContactPlayerData(UUID.fromString(uuid), contacts.associateBy(PokenavContact::contactId).toMutableMap())
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
    }

    private val player: ServerPlayer? by lazy { uuid.getPlayer() }

    fun findByUuid(uuid: UUID): PokenavContact? {
        val contactId = ContactID(uuid)
        return contacts[contactId]
    }

    fun findByName(name: String): PokenavContact? = contacts.values.firstOrNull { it.name == name }

    fun addContact(contact: PokenavContact): Boolean {
        if (contacts.putIfAbsent(contact.contactId, contact) != null) return false
        CobblenavEvents.CONTACTS_ADDED.post(ContactsAdded(player, listOf(contact)))
        onContactListUpdated()
        return true
    }

    fun addContacts(contacts: List<PokenavContact>): Boolean {
        val added = contacts.filter { this.contacts.putIfAbsent(it.contactId, it) == null }
        if (added.isEmpty()) return false
        CobblenavEvents.CONTACTS_ADDED.post(ContactsAdded(player, added))
        onContactListUpdated()
        return true
    }

    fun removeContact(contactID: ContactID): Boolean {
        val contact = contacts.remove(contactID) ?: return false
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
                playerData = ClientContactPlayerData(contacts.mapValues { it.value.toClientContact() }.toMutableMap())
            )
        }
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientContactPlayerData(contacts.mapValues { it.value.toClientContact() }.toMutableMap())
    }
}