package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import com.metacontent.cobblenav.storage.client.ClientSpawnDataCatalogue
import com.metacontent.cobblenav.util.getSpawnDataCatalogue
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerPlayer
import java.util.*

class SpawnDataCatalogue(
    override val uuid: UUID,
    override val spawnDetailIds: MutableSet<String>
) : AbstractSpawnDataCatalogue(), InstancedPlayerData {
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
            if (it) {
                onAdded(setOf(id))
            }
        }
    }

    fun catalogue(ids: Iterable<String>): Boolean {
        val added = ids.filter { spawnDetailIds.add(it) }.toSet()
        if (added.isNotEmpty()) {
            onAdded(added)
            return true
        }
        return false
    }

    fun remove(id: String): Boolean {
        return spawnDetailIds.remove(id)
    }

    fun remove(ids: Set<String>): Boolean {
        return spawnDetailIds.removeAll(ids)
    }

    fun clear(): Boolean {
        if (spawnDetailIds.isNotEmpty()) {
            spawnDetailIds.clear()
            return true
        }
        return false
    }

    private fun onAdded(entries: Set<String>) {
        player?.let {
            SetClientPlayerDataPacket(
                type = CobblenavDataStoreTypes.SPAWN_DATA,
                playerData = collectClientData(entries),
                isIncremental = true
            ).sendToPlayer(it)
        }
    }

    override fun toClientData(): ClientSpawnDataCatalogue = collectClientData(spawnDetailIds)

    private fun collectClientData(spawnDetailIds: Set<String>): ClientSpawnDataCatalogue {
        val data = player?.let { player ->
            spawnDetailIds.associateWith { SpawnDataHelper.getSpawnData(it, player) }.toMutableMap()
        } ?: mutableMapOf()
        return ClientSpawnDataCatalogue(data)
    }
}