package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.metacontent.cobblenav.api.contact.ClientPokenavContact
import net.minecraft.network.RegistryFriendlyByteBuf

data class ClientContactPlayerData(
    val contacts: MutableList<ClientPokenavContact> = mutableListOf()
) : ClientInstancedPlayerData {
    companion object {
        fun decode(buf: RegistryFriendlyByteBuf): SetClientPlayerDataPacket = SetClientPlayerDataPacket(
            type = CobblenavDataStoreTypes.CONTACTS,
            playerData = ClientContactPlayerData(
                contacts = buf.readList { ClientPokenavContact.decode(it as RegistryFriendlyByteBuf) }
            )
        )
    }

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeCollection(contacts) { pb, value -> value.encode(pb as RegistryFriendlyByteBuf) }
    }
}