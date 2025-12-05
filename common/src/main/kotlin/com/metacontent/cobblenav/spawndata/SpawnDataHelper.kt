package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.fishing.FishingSpawnCause
import com.cobblemon.mod.common.api.spawning.influence.BucketNormalizingInfluence
import com.cobblemon.mod.common.api.spawning.influence.PlayerLevelRangeInfluence
import com.cobblemon.mod.common.api.spawning.influence.PlayerLevelRangeInfluence.Companion.TYPICAL_VARIATION
import com.cobblemon.mod.common.api.spawning.position.FishingSpawnablePosition
import com.cobblemon.mod.common.api.spawning.position.calculators.SpawnablePositionCalculator
import com.cobblemon.mod.common.api.spawning.spawner.SpawningZoneInput
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.util.spawner
import com.cobblemon.mod.common.util.toBlockPos
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.api.platform.BiomePlatforms
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.spawndata.resultdata.SpawnResultData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.math.ceil

object SpawnDataHelper {
    fun checkPlayerSpawns(player: ServerPlayer, bucket: String): List<SpawnData> {
        val cobblemonConfig = Cobblemon.config
        val config = Cobblenav.config
        val spawnDataList = mutableListOf<SpawnData>()

        if (cobblemonConfig.enableSpawning) {
            val spawner = player.spawner
            val bucket = Cobblemon.bestSpawner.config.buckets.firstOrNull { it.name == bucket } ?: run {
                Cobblenav.LOGGER.error("For some reason bucket is null")
                return emptyList()
            }

            val cause = SpawnCause(spawner, player)
            val zone = Cobblemon.spawningZoneGenerator.generate(
                spawner = spawner,
                input = SpawningZoneInput(
                    cause, player.serverLevel(),
                    ceil(player.x - config.checkSpawnWidth / 2f).toInt(),
                    ceil(player.y - config.checkSpawnHeight / 2f).toInt(),
                    ceil(player.z - config.checkSpawnWidth / 2f).toInt(),
                    config.checkSpawnWidth,
                    config.checkSpawnHeight,
                    config.checkSpawnWidth
                )
            )
            val spawnablePositions = Cobblenav.resolver.resolve(
                spawner = spawner,
                spawnablePositionCalculators = SpawnablePositionCalculator.prioritizedAreaCalculators,
                zone = zone
            )
            val spawnProbabilities = spawner.selector.getProbabilities(spawner, bucket, spawnablePositions)

            spawnProbabilities.forEach { (detail, spawnChance) ->
                if (detail.isValid()) {
                    collect(detail, spawnChance, player)?.let { spawnDataList.add(it) }
                }
            }
        }

        return spawnDataList
    }

    fun checkFishingSpawns(player: ServerPlayer): Map<String, List<SpawnData>> {
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

        return Cobblemon.bestSpawner.config.buckets.associate { bucket ->
            bucket.name to spawner.selector.getProbabilities(spawner, bucket, spawnablePositions)
                .mapNotNull { detailProbability ->
                    collect(detailProbability.key, detailProbability.value, player)
                }
        }
    }

    fun collect(
        detail: SpawnDetail,
        spawnChance: Float,
        player: ServerPlayer
    ): SpawnData? {
        val result = SpawnResultData.fromDetail(detail, player) ?: return null

        val builder = BiomePlatformContext.Builder()
        val conditions = mutableListOf<ConditionData>()
        val blockConditions = mutableSetOf<ResourceLocation>()
        val anticonditions = mutableListOf<ConditionData>()
        val blockAnticonditions = mutableSetOf<ResourceLocation>()
        val canBeShowed = !result.isUnknown() || !Cobblenav.config.hideConditionsOfUnknownSpawns
        if (canBeShowed) {
            detail.conditions.forEach { condition ->
                conditions += ConditionCollectors.collectConditions(detail, condition, player, builder)
                blockConditions += ConditionCollectors.collectBlockConditions(condition)
            }
            detail.anticonditions.forEach { condition ->
                anticonditions += ConditionCollectors.collectConditions(detail, condition, player)
                blockAnticonditions += ConditionCollectors.collectBlockConditions(condition)
            }
        }
        val platformId = BiomePlatforms.firstFitting(builder.build())

        return SpawnData(
            id = if (canBeShowed) detail.id else "???",
            result = result,
            positionType = detail.spawnablePositionType.name,
            spawnChance = spawnChance,
            platformId = platformId,
            conditions = conditions,
            anticonditions = anticonditions,
            blockConditions = BlockConditions(blockConditions),
            blockAnticonditions = BlockConditions(blockAnticonditions)
        )
    }
}