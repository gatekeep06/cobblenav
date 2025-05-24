package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.fishing.FishingSpawnCause
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.util.toBlockPos
import com.metacontent.cobblenav.networking.packet.client.FishingMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestFishingMapPacket
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object RequestFishingMapHandler : ServerNetworkPacketHandler<RequestFishingMapPacket> {
    override fun handle(packet: RequestFishingMapPacket, server: MinecraftServer, player: ServerPlayer) {
        server.execute {
            val bobber = player.fishing
            val rods = if (bobber is PokeRodFishingBobberEntity && bobber.rodStack != null) {
                listOf(bobber.rodStack!!)
            }
            else {
                (player.inventory.items + player.offhandItem).filter { it.`is`(CobblemonItemTags.POKE_RODS) }
            }
            val spawner = Cobblemon.bestSpawner.fishingSpawner
            val causesMap = Cobblemon.bestSpawner.config.buckets.associate { bucket ->
                bucket.name to rods.map { rod ->
                    FishingSpawnCause(spawner, bucket, player, rod)
                }
            }
            val pos = player.fishing?.position()?.toBlockPos() ?: player.position().toBlockPos()
            val contexts = causesMap.mapValues { entry ->
                entry.value.mapNotNull { causes ->
                    spawner.parseContext(causes, player.serverLevel(), pos)
                }
            }
            val spawnDataMap = contexts.mapValues { entry ->
                spawner.getSpawningSelector().getProbabilities(spawner, entry.value).mapNotNull { detailProbability ->
                    (detailProbability.key as? PokemonSpawnDetail)?.let {
                        SpawnDataHelper.collect(it, detailProbability.value, entry.value, player)
                    }
                }
            }
            FishingMapPacket(spawnDataMap).sendToPlayer(player)
        }
    }
}