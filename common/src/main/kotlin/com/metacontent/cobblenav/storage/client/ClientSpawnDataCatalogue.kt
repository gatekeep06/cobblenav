package com.metacontent.cobblenav.storage.client

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.PokenavSignalManager
import com.metacontent.cobblenav.client.gui.PokenavSignalManager.SPAWN_CATALOGUED_SIGNAL
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.storage.AbstractSpawnDataCatalogue
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import net.minecraft.network.RegistryFriendlyByteBuf

class ClientSpawnDataCatalogue(
    val spawnData: MutableMap<String, List<SpawnData>> = mutableMapOf()
) : AbstractSpawnDataCatalogue(), ClientInstancedPlayerData {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf): SetClientPlayerDataPacket = SetClientPlayerDataPacket(
            type = CobblenavDataStoreTypes.SPAWN_DATA,
            playerData = ClientSpawnDataCatalogue()
        )

        fun afterDecode(data: ClientInstancedPlayerData) {
            (data as? ClientSpawnDataCatalogue)?.let {
                CobblenavClient.spawnDataCatalogue = it
            }
        }

        fun incrementalAfterDecode(data: ClientInstancedPlayerData) {
            (data as? ClientSpawnDataCatalogue)?.let {
                PokenavSignalManager.add(SPAWN_CATALOGUED_SIGNAL.copy())
                val current = CobblenavClient.spawnDataCatalogue
                current.spawnData.putAll(data.spawnData)
                CobblenavClient.spawnDataCatalogue.newlyCatalogued.addAll(data.spawnDetailIds)
            }
        }
    }

    override val spawnDetailIds: MutableSet<String>
        get() = spawnData.keys

    private val newlyCatalogued = mutableSetOf<String>()

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeCollection(spawnDetailIds) { b, s -> b.writeString(s) }
    }

    fun missingCachedData(): List<String> = if (spawnData.isEmpty()) {
        spawnDetailIds.toList()
    } else {
        spawnDetailIds.filter { !spawnData.contains(it) }
    }
}