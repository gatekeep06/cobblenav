package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer
import com.metacontent.cobblenav.storage.client.ClientSpawnDataCatalogue
import com.metacontent.cobblenav.util.getSpawnDataCatalogue
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerPlayer
import java.util.*

class SpawnDataCatalogue(
    override val uuid: UUID,
    spawnDetailIds: MutableSet<String>
) : AbstractSpawnDataCatalogue(spawnDetailIds), InstancedPlayerData {
    companion object {
        val CODEC: Codec<SpawnDataCatalogue> = RecordCodecBuilder.create<SpawnDataCatalogue> { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                Codec.STRING.listOf().fieldOf("spawnDetailIds").forGetter { it.spawnDetailIds.toList() }
            ).apply(instance) { uuid, ids ->
                SpawnDataCatalogue(UUID.fromString(uuid), ids.toMutableSet())
            }
        }

        fun executeAndSave(uuid: UUID, action: (SpawnDataCatalogue) -> Boolean): Boolean {
            val data = Cobblemon.playerDataManager.getSpawnDataCatalogue(uuid)
            return action(data).also {
                if (it) {
                    Cobblemon.playerDataManager.saveSingle(data, CobblenavDataStoreTypes.SPAWN_DATA)
                }
            }
        }

        fun executeAndSave(player: ServerPlayer, action: (SpawnDataCatalogue) -> Boolean): Boolean {
            return executeAndSave(player.uuid, action)
        }
    }

    private val player: ServerPlayer? by lazy { uuid.getPlayer() }

    fun catalogue(id: String): Boolean {
        return spawnDetailIds.add(id).also {
            if (it) onAdded(listOf(id))
        }
    }

    fun catalogue(ids: Iterable<String>): Boolean {
        val addedIds = ids.filter { spawnDetailIds.add(it) }
        if (addedIds.isNotEmpty()) {
            onAdded(addedIds)
            return true
        }
        return false
    }

    fun remove(id: String): Boolean {
        return spawnDetailIds.remove(id).also {
            if (it) onRemoved(listOf(id))
        }
    }

    fun remove(ids: Iterable<String>): Boolean {
        val removedIds = mutableListOf<String>()
        spawnDetailIds.removeAll { id ->
            ids.contains(id).also {
                if (it) removedIds.add(id)
            }
        }
        if (removedIds.isNotEmpty()) {
            onRemoved(removedIds)
            return true
        }
        return false
    }

    fun clear(): Boolean {
        if (spawnDetailIds.isNotEmpty()) {
            spawnDetailIds.clear()
            player?.let {
                SetClientPlayerDataPacket(
                    type = CobblenavDataStoreTypes.SPAWN_DATA,
                    playerData = toClientData()
                ).sendToPlayer(it)
            }
            return true
        }
        return false
    }

    private fun onAdded(ids: Iterable<String>) {

    }

    private fun onRemoved(ids: Iterable<String>) {

    }

    override fun toClientData() = ClientSpawnDataCatalogue(spawnDetailIds)
}