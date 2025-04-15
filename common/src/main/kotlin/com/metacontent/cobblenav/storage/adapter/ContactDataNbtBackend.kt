package com.metacontent.cobblenav.storage.adapter

import com.cobblemon.mod.common.api.storage.player.adapter.NbtBackedPlayerData
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import com.metacontent.cobblenav.storage.ContactPlayerData
import com.mojang.serialization.Codec
import java.util.*

class ContactDataNbtBackend : NbtBackedPlayerData<ContactPlayerData>(
    subfolder = "cobblenav/contacts",
    type = CobblenavDataStoreTypes.CONTACTS
) {
    override val codec: Codec<ContactPlayerData> = ContactPlayerData.CODEC
    override val defaultData = { uuid: UUID -> ContactPlayerData(uuid, mutableMapOf()) }
}