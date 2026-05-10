package com.metacontent.cobblenav.storage.client

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.removeIf
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.CobblenavClient
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
                if (it.spawnDetailIds.isNotEmpty()) {
                    RequestCatalogueDataPacket(CobblenavClient.spawnDataCatalogue.missingCachedData()).sendToServer()
                }
            }
        }
    }

    val newEntries = mutableSetOf<String>()
    val cachedSpawnData = mutableMapOf<String, List<SpawnData>>()

    internal fun add(entries: Map<String, List<SpawnData>>) {
        spawnDetailIds.addAll(entries.keys)
        newEntries.addAll(entries.keys)
        cachedSpawnData.putAll(entries)
    }

    internal fun remove(ids: Set<String>) {
        spawnDetailIds.removeAll(ids)
        newEntries.removeAll(ids)
        cachedSpawnData.removeIf { (key, _) -> ids.contains(key) }
    }

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeCollection(spawnDetailIds) { b, s -> b.writeString(s) }
    }

    fun missingCachedData(): List<String> = if (cachedSpawnData.isEmpty()) {
        spawnDetailIds.toList()
    } else {
        spawnDetailIds.filter { !cachedSpawnData.contains(it) }
    }
}