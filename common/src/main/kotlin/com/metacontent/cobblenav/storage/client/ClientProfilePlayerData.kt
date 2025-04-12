package com.metacontent.cobblenav.storage.client

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.metacontent.cobblenav.api.contact.title.TrainerTitle
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import java.util.UUID

data class ClientProfilePlayerData(
    var title: TrainerTitle? = null,
    val allowedTitles: MutableSet<TrainerTitle> = mutableSetOf(),
    var partnerPokemonUuid: UUID? = null
) : ClientInstancedPlayerData {
    companion object {
        fun decode(buf: RegistryFriendlyByteBuf): SetClientPlayerDataPacket = SetClientPlayerDataPacket(
            type = CobblenavDataStoreTypes.PROFILE,
            playerData = ClientProfilePlayerData(
                title = buf.readNullable { TrainerTitle.decode(buf) },
                allowedTitles = buf.readList { TrainerTitle.decode(it as RegistryFriendlyByteBuf) }.toMutableSet(),
                partnerPokemonUuid = buf.readNullable { buf.readUUID() }
            )
        )

        fun afterDecode(data: ClientInstancedPlayerData) {
            if (data !is ClientProfilePlayerData) return
            CobblenavClient.clientProfileData = data
        }

        fun incrementalAfterDecode(data: ClientInstancedPlayerData) {
            if (data !is ClientProfilePlayerData) return
            CobblenavClient.clientProfileData.allowedTitles.addAll(data.allowedTitles)
        }
    }

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeNullable(title) { pb, value -> value.encode(pb as RegistryFriendlyByteBuf) }
        buf.writeCollection(allowedTitles) { pb, value -> value.encode(pb as RegistryFriendlyByteBuf) }
        buf.writeNullable(partnerPokemonUuid) { pb, value -> pb.writeUUID(value) }
    }
}