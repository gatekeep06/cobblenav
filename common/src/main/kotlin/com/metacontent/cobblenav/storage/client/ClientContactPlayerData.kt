package com.metacontent.cobblenav.storage.client

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.metacontent.cobblenav.api.contact.ClientPokenavContact
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import java.util.UUID

data class ClientContactPlayerData(
    val contacts: MutableMap<UUID, ClientPokenavContact> = mutableMapOf()
) : ClientInstancedPlayerData {
    companion object {
        fun decode(buf: RegistryFriendlyByteBuf): SetClientPlayerDataPacket = SetClientPlayerDataPacket(
            type = CobblenavDataStoreTypes.CONTACTS,
            playerData = ClientContactPlayerData(
                contacts = buf.readMap(
                    { it.readUUID() },
                    { ClientPokenavContact.decode(it as RegistryFriendlyByteBuf) }
                )
            )
        )

        fun afterDecode(data: ClientInstancedPlayerData) {
            if (data !is ClientContactPlayerData) return
            CobblenavClient.clientContactData = data
        }

        fun incrementalAfterDecode(data: ClientInstancedPlayerData) {
            if (data !is ClientContactPlayerData) return
            CobblenavClient.clientContactData.contacts.putAll(data.contacts)
        }
    }

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeMap(
            contacts,
            { pb, key -> pb.writeUUID(key) },
            { pb, value -> value.encode(pb as RegistryFriendlyByteBuf) }
        )
    }
}