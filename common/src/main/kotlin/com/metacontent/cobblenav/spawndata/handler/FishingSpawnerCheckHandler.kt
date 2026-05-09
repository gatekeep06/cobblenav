package com.metacontent.cobblenav.spawndata.handler

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.fishing.FishingSpawnCause
import com.cobblemon.mod.common.api.spawning.influence.BucketNormalizingInfluence
import com.cobblemon.mod.common.api.spawning.influence.PlayerLevelRangeInfluence
import com.cobblemon.mod.common.api.spawning.influence.PlayerLevelRangeInfluence.Companion.TYPICAL_VARIATION
import com.cobblemon.mod.common.api.spawning.position.FishingSpawnablePosition
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.util.toBlockPos
import com.metacontent.cobblenav.spawndata.CheckedSpawnData
import com.metacontent.cobblenav.spawndata.SpawnDataHelper.BASE_FISHING_POKEMON_CHANCE
import com.metacontent.cobblenav.spawndata.SpawnDataHelper.calculateWeightedBuckets
import com.metacontent.cobblenav.spawndata.SpawnDataHelper.getSpawnData
import net.minecraft.server.level.ServerPlayer
import kotlin.collections.component1
import kotlin.collections.component2

interface FishingSpawnerCheckHandler {
    fun checkFishingSpawns(player: ServerPlayer): Map<String, List<CheckedSpawnData>>

    companion object : FishingSpawnerCheckHandler {
        override fun checkFishingSpawns(player: ServerPlayer): Map<String, List<CheckedSpawnData>> {
            val bobber = player.fishing
            val rods = if (bobber is PokeRodFishingBobberEntity && bobber.rodStack != null) {
                listOf(bobber.rodStack!!)
            } else {
                (player.inventory.items + player.offhandItem).filter { it.`is`(CobblemonItemTags.POKE_RODS) }
            }

            val pokemonChance = (bobber as? PokeRodFishingBobberEntity)
                ?.rodStack
                ?.let { bobber.getPokemonSpawnChance(it) / 100f } ?: BASE_FISHING_POKEMON_CHANCE

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

            val bucketWeights = Cobblemon.bestSpawner.config.buckets.associateWith { it.weight }.toMutableMap()
            val influences = if (spawnablePositions.size == 1) {
                spawnablePositions.first().influences + spawner.influences
            } else {
                spawner.influences
            }
            val weightedBuckets = calculateWeightedBuckets(bucketWeights, influences).associate { (name, chance) ->
                name to chance
            }

            return bucketWeights.keys.associate { bucket ->
                bucket.name to spawner.selector.getProbabilities(spawner, bucket, spawnablePositions)
                    .mapNotNull { (detail, chance) ->
                        getSpawnData(detail, player)?.let {
                            CheckedSpawnData(it, null, chance * pokemonChance * (weightedBuckets[bucket.name] ?: 1f))
                        }
                    }
            }
        }
    }
}