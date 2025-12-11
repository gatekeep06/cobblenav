package com.metacontent.cobblenav.storage.client

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import net.minecraft.network.RegistryFriendlyByteBuf

class ClientSpawnDataCatalogue(
    val spawnDetailIds: MutableSet<String> = mutableSetOf()
) : ClientInstancedPlayerData {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf): SetClientPlayerDataPacket = SetClientPlayerDataPacket(
            type = CobblenavDataStoreTypes.SPAWN_DATA,
            playerData = ClientSpawnDataCatalogue(
                spawnDetailIds = buffer.readList { it.readString() }.toMutableSet()
            )
        )

        fun afterDecode(data: ClientInstancedPlayerData) {
            (data as? ClientSpawnDataCatalogue)?.let {
                CobblenavClient.spawnDataCatalogue = it
            }
        }

        fun incrementalAfterDecode(data: ClientInstancedPlayerData) {
            (data as? ClientSpawnDataCatalogue)?.let {
                CobblenavClient.spawnDataCatalogue.spawnDetailIds.addAll(data.spawnDetailIds)
                Cobblenav.LOGGER.error(CobblenavClient.spawnDataCatalogue.spawnDetailIds.joinToString())
            }
        }
    }

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeCollection(spawnDetailIds) { b, s -> b.writeString(s) }
    }
}