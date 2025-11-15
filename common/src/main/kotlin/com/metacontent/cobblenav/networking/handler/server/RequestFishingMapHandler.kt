package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.fishing.FishingSpawnCause
import com.cobblemon.mod.common.api.spawning.influence.BucketNormalizingInfluence
import com.cobblemon.mod.common.api.spawning.influence.PlayerLevelRangeInfluence
import com.cobblemon.mod.common.api.spawning.influence.PlayerLevelRangeInfluence.Companion.TYPICAL_VARIATION
import com.cobblemon.mod.common.api.spawning.position.FishingSpawnablePosition
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
            } else {
                (player.inventory.items + player.offhandItem).filter { it.`is`(CobblemonItemTags.POKE_RODS) }
            }

            val spawner = Cobblemon.bestSpawner.fishingSpawner
            val lureLevel = (bobber as? PokeRodFishingBobberEntity)?.lureLevel ?: 0
            val luckOfTheSea = (bobber as? PokeRodFishingBobberEntity)?.luckOfTheSeaLevel ?: 0
            val causes = rods.map { rod ->
                FishingSpawnCause(spawner, player, rod, lureLevel)
            }
            val pos = player.fishing?.position()?.toBlockPos() ?: player.position().toBlockPos()
            val bucketInfluence = BucketNormalizingInfluence(tier = lureLevel + luckOfTheSea)
            val spawnablePositions = causes.map { cause ->
                FishingSpawnablePosition(
                    cause = cause,
                    world = player.serverLevel(),
                    pos = pos,
                    influences = mutableListOf(
                        PlayerLevelRangeInfluence(player, TYPICAL_VARIATION),
                        bucketInfluence
                    )
                )
            }

            val spawnDataMap = Cobblemon.bestSpawner.config.buckets.associate { bucket ->
                bucket.name to spawner.selector.getProbabilities(spawner, bucket, spawnablePositions)
                    .mapNotNull { detailProbability ->
                        (detailProbability.key as? PokemonSpawnDetail)?.let {
                            SpawnDataHelper.collect(it, detailProbability.value, spawnablePositions, player)
                        }
                    }
            }

            FishingMapPacket(spawnDataMap).sendToPlayer(player)
        }
    }
}