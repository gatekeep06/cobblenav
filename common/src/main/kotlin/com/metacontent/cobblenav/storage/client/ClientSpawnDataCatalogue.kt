package com.metacontent.cobblenav.storage.client

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.PokenavSignalManager
import com.metacontent.cobblenav.client.gui.PokenavSignalManager.SPAWN_CATALOGUED_SIGNAL
import com.metacontent.cobblenav.networking.packet.server.RequestCatalogueDataPacket
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.storage.AbstractSpawnDataCatalogue
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import net.minecraft.network.RegistryFriendlyByteBuf

class ClientSpawnDataCatalogue(
    spawnDetailIds: MutableSet<String> = mutableSetOf()
) : AbstractSpawnDataCatalogue(spawnDetailIds), ClientInstancedPlayerData {
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
                RequestCatalogueDataPacket(CobblenavClient.spawnDataCatalogue.missingCachedData()).sendToServer()
            }
        }

        fun incrementalAfterDecode(data: ClientInstancedPlayerData) {
            (data as? ClientSpawnDataCatalogue)?.let {
                PokenavSignalManager.add(SPAWN_CATALOGUED_SIGNAL.copy())
                val current = CobblenavClient.spawnDataCatalogue.spawnDetailIds
                val updated = it.spawnDetailIds
                CobblenavClient.spawnDataCatalogue = it
                CobblenavClient.spawnDataCatalogue.newlyCataloguedAmount += (updated.size - current.size).coerceAtLeast(0)

                if (CobblenavClient.spawnDataCatalogue.missingCachedData().size > 100) {
                    RequestCatalogueDataPacket(CobblenavClient.spawnDataCatalogue.missingCachedData()).sendToServer()
                }
            }
        }
    }

    val cachedSpawnData = mutableMapOf<String, List<SpawnData>>()

    var newlyCataloguedAmount = 0
        internal set

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeCollection(spawnDetailIds) { b, s -> b.writeString(s) }
    }

    fun missingCachedData(): List<String> = if (cachedSpawnData.isEmpty()) {
        spawnDetailIds.toList()
    } else {
        spawnDetailIds.filter { !cachedSpawnData.contains(it) }
    }
}