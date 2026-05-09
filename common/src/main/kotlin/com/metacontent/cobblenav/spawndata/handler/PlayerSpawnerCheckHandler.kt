package com.metacontent.cobblenav.spawndata.handler

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.api.spawning.position.calculators.SpawnablePositionCalculator
import com.cobblemon.mod.common.api.spawning.spawner.SpawningZoneInput
import com.cobblemon.mod.common.util.spawner
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.api.platform.BiomePlatforms
import com.metacontent.cobblenav.spawndata.CheckedSpawnData
import com.metacontent.cobblenav.spawndata.SpawnDataHelper.calculateWeightedBuckets
import com.metacontent.cobblenav.spawndata.SpawnDataHelper.getSpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import net.minecraft.server.level.ServerPlayer
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.ceil

interface PlayerSpawnerCheckHandler {
    fun checkPlayerSpawns(player: ServerPlayer, bucketName: String): Pair<WeightedBucket, List<CheckedSpawnData>>

    companion object : PlayerSpawnerCheckHandler {
        override fun checkPlayerSpawns(
            player: ServerPlayer,
            bucketName: String
        ): Pair<WeightedBucket, List<CheckedSpawnData>> {
            val config = Cobblemon.config

            if (!config.enableSpawning) return WeightedBucket(bucketName, 0f) to emptyList()

            val spawner = player.spawner

            val bucketWeights = Cobblemon.bestSpawner.config.buckets.associateWith { it.weight }.toMutableMap()
            val bucket = bucketWeights.keys.firstOrNull { it.name == bucketName } ?: run {
                Cobblenav.LOGGER.error("For some reason bucket is null")
                return WeightedBucket(bucketName, 0f) to emptyList()
            }

            val cause = SpawnCause(spawner, player)
            val zone = Cobblemon.spawningZoneGenerator.generate(
                spawner = spawner,
                input = SpawningZoneInput(
                    cause, player.serverLevel(),
                    ceil(player.x - config.spawningZoneDiameter / 2f).toInt(),
                    ceil(player.y - config.spawningZoneHeight / 2f).toInt(),
                    ceil(player.z - config.spawningZoneDiameter / 2f).toInt(),
                    config.spawningZoneDiameter,
                    config.spawningZoneHeight,
                    config.spawningZoneDiameter
                )
            )
            val spawnablePositions = Cobblenav.resolver.resolve(
                spawner = spawner,
                spawnablePositionCalculators = SpawnablePositionCalculator.prioritizedAreaCalculators,
                zone = zone
            )
            val spawnProbabilities = spawner.selector.getProbabilities(spawner, bucket, spawnablePositions)

            val spawnDataList = spawnProbabilities.mapNotNull { (detail, spawnChance) ->
                val fittingPositions = spawnablePositions.filter { detail.isSatisfiedBy(it) }
                val platformId = BiomePlatforms.firstFitting(fittingPositions)
                getSpawnData(detail, player)?.let { CheckedSpawnData(it, platformId, spawnChance) }
            }

            val weightedBucket = calculateWeightedBuckets(
                bucketWeights,
                spawner.influences + zone.unconditionalInfluences
            ).first { it.name == bucketName }

            return weightedBucket to spawnDataList
        }
    }
}

