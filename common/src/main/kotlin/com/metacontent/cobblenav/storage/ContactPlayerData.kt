package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.metacontent.cobblenav.api.contact.ContactID
import com.metacontent.cobblenav.api.contact.PokenavContact
import com.metacontent.cobblenav.storage.client.ClientContactPlayerData
import java.util.*

data class ContactPlayerData(
    override val uuid: UUID,
    val contacts: MutableMap<ContactID, PokenavContact>
) : InstancedPlayerData {
    override fun toClientData(): ClientInstancedPlayerData {
        return ClientContactPlayerData(contacts.values.map { it.toClientContact() }.toMutableList())
    }
}