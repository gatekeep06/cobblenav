package com.metacontent.cobblenav.storage.client

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.readMap
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.api.contact.BattleId
import com.metacontent.cobblenav.api.contact.BattleRecord
import com.metacontent.cobblenav.api.contact.ClientPokenavContact
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import java.util.*

data class ClientContactPlayerData(
    val contacts: HashMap<String, ClientPokenavContact> = hashMapOf(),
    val battles: HashMap<BattleId, BattleRecord> = hashMapOf()
) : ClientInstancedPlayerData {
    companion object {
        fun decode(buf: RegistryFriendlyByteBuf): SetClientPlayerDataPacket = SetClientPlayerDataPacket(
            type = CobblenavDataStoreTypes.CONTACTS,
            playerData = ClientContactPlayerData(
                contacts = HashMap(
                    buf.readMap(
                        { it.readString() },
                        { ClientPokenavContact.decode(it as RegistryFriendlyByteBuf) }
                    )),
                battles = HashMap(
                    buf.readMap(
                        { BattleId.decode(it as RegistryFriendlyByteBuf) },
                        { BattleRecord.decode(it as RegistryFriendlyByteBuf) }
                    )
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
            CobblenavClient.clientContactData.battles.putAll(data.battles)
        }
    }

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeMap(
            contacts,
            { pb, key -> pb.writeString(key) },
            { pb, value -> value.encode(pb as RegistryFriendlyByteBuf) }
        )
        buf.writeMap(
            battles,
            { pb, key -> key.encode(pb as RegistryFriendlyByteBuf) },
            { pb, value -> value.encode(pb as RegistryFriendlyByteBuf) }
        )
    }
}