package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer
import com.metacontent.cobblenav.storage.client.ClientSpawnDataCatalogue
import com.metacontent.cobblenav.util.getSpawnDataCatalogue
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
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
            if (it) {
                player?.sendSystemMessage(Component.translatable("gui.cobblenav.notification.catalogue_updated", id))
                onCatalogueUpdated()
            }
        }
    }

    private fun onCatalogueUpdated() {
        player?.let {
            SetClientPlayerDataPacket(
                type = CobblenavDataStoreTypes.SPAWN_DATA,
                playerData = toClientData(),
                isIncremental = true
            ).sendToPlayer(it)
        }
    }

    override fun toClientData() = ClientSpawnDataCatalogue(spawnDetailIds)
}