package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.spawning.SpawnBucket
import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.fishing.FishingSpawnCause
import com.cobblemon.mod.common.api.spawning.influence.BucketNormalizingInfluence
import com.cobblemon.mod.common.api.spawning.influence.PlayerLevelRangeInfluence
import com.cobblemon.mod.common.api.spawning.influence.PlayerLevelRangeInfluence.Companion.TYPICAL_VARIATION
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.api.spawning.position.FishingSpawnablePosition
import com.cobblemon.mod.common.api.spawning.position.calculators.SpawnablePositionCalculator
import com.cobblemon.mod.common.api.spawning.spawner.SpawningZoneInput
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.spawner
import com.cobblemon.mod.common.util.toBlockPos
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.api.platform.BiomePlatforms
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.properties.SpawnDetailIdPropertyType
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.spawndata.resultdata.SpawnResultData
import com.metacontent.cobblenav.storage.SpawnDataCatalogue
import com.metacontent.cobblenav.util.WeightedBucket
import com.metacontent.cobblenav.util.spawnCatalogue
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.math.ceil

object SpawnDataHelper {
    const val BASE_FISHING_POKEMON_CHANCE = 0.85f

    fun calculateWeightedBuckets(
        bucketWeights: MutableMap<SpawnBucket, Float>,
        influences: List<SpawningInfluence>
    ): List<WeightedBucket> {
        influences.forEach { it.affectBucketWeights(bucketWeights) }
        val sum = bucketWeights.values.sum()
        return bucketWeights.map { (key, value) -> WeightedBucket(key.name, value / sum) }
    }

    fun checkPlayerSpawns(
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
            collect(detail, player)?.let { CheckedSpawnData(it, spawnChance) }
        }

        val weightedBucket = calculateWeightedBuckets(
            bucketWeights,
            spawner.influences + zone.unconditionalInfluences
        ).first { it.name == bucketName }

        return weightedBucket to spawnDataList
    }

    fun checkFishingSpawns(
        player: ServerPlayer
    ): Map<String, List<CheckedSpawnData>> {
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
        val weightedBuckets = calculateWeightedBuckets(bucketWeights, influences).associate {
            (name, chance) -> name to chance
        }

        return bucketWeights.keys.associate { bucket ->
            bucket.name to spawner.selector.getProbabilities(spawner, bucket, spawnablePositions)
                .mapNotNull { (detail, chance) ->
                    collect(detail, player)?.let {
                        CheckedSpawnData(it, chance * pokemonChance * (weightedBuckets[bucket.name] ?: 1f))
                    }
                }
        }
    }

    fun checkFixedAreaSpawns(
        areaPoint: BlockPos,
        player: ServerPlayer,
        bucketName: String
    ): Pair<WeightedBucket, List<CheckedSpawnData>> {
        val spawner = player.serverLevel().getBlockEntity(areaPoint)?.let {
            (it as? PokeSnackBlockEntity)?.spawner
        } ?: return WeightedBucket(bucketName, 0f) to emptyList()

        val bucketWeights = Cobblemon.bestSpawner.config.buckets.associateWith { it.weight }.toMutableMap()
        val bucket = bucketWeights.keys.firstOrNull { it.name == bucketName } ?: run {
            Cobblenav.LOGGER.error("For some reason bucket is null")
            return WeightedBucket(bucketName, 0f) to emptyList()
        }

        val cause = SpawnCause(spawner, player)
        val zone = Cobblemon.spawningZoneGenerator.generate(
            spawner = spawner,
            input = spawner.getZoneInput(cause)
        )
        val spawnablePositions = Cobblenav.resolver.resolve(
            spawner = spawner,
            spawnablePositionCalculators = SpawnablePositionCalculator.prioritizedAreaCalculators,
            zone = zone
        )

        val spawnProbabilities = spawner.selector.getProbabilities(spawner, bucket, spawnablePositions)

        val spawnDataList = spawnProbabilities.mapNotNull { (detail, spawnChance) ->
            collect(detail, player)?.let { CheckedSpawnData(it, spawnChance) }
        }

        val weightedBucket = calculateWeightedBuckets(
            bucketWeights,
            spawner.influences + zone.unconditionalInfluences
        ).first { it.name == bucketName }

        return weightedBucket to spawnDataList
    }

    fun collect(
        detail: SpawnDetail,
        player: ServerPlayer
    ): SpawnData? {
        val result = SpawnResultData.fromDetail(detail, player) ?: return null

        val builder = if (result.shouldRenderPlatform()) BiomePlatformContext.Builder() else null
        val conditions = mutableListOf<ConditionData>()
        val blockConditions = mutableSetOf<ResourceLocation>()
        val anticonditions = mutableListOf<ConditionData>()
        val blockAnticonditions = mutableSetOf<ResourceLocation>()
        val canShowConditions = !Cobblenav.config.hideConditionsOfUnknownSpawns || player.spawnCatalogue().contains(detail)
        if (canShowConditions) {
            detail.conditions.forEach { condition ->
                conditions += ConditionCollectors.collectConditions(detail, condition, player, builder)
                blockConditions += ConditionCollectors.collectBlockConditions(condition)
            }
            detail.anticonditions.forEach { condition ->
                anticonditions += ConditionCollectors.collectConditions(detail, condition, player)
                blockAnticonditions += ConditionCollectors.collectBlockConditions(condition)
            }
        } else {
            val condition = ConditionData("unknown", 0xffffff, emptyList())
            conditions += condition
            anticonditions += condition
        }
        val platformId = builder?.build()?.let { BiomePlatforms.firstFitting(it) }

        return SpawnData(
            id = if (!result.isUnknown() || !Cobblenav.config.hideUnknownSpawns) detail.id else "???",
            result = result,
            positionType = detail.spawnablePositionType.name,
            bucket = detail.bucket.name,
            weight = detail.weight,
            platformId = platformId,
            conditions = conditions,
            anticonditions = anticonditions,
            blockConditions = BlockConditions(blockConditions),
            blockAnticonditions = BlockConditions(blockAnticonditions)
        )
    }

    fun onInit() {
        CobblenavEvents.POKEMON_ENCOUNTERED.subscribe { (pokemon, player) ->
            player?.catalogueDetailId(pokemon)
        }

        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe { event ->
            event.spawnablePosition.cause.entity?.let {
                if (it is ServerPlayer) it.catalogueDetailId(event.entity.pokemon)
            }
        }

        CobblemonEvents.BOBBER_SPAWN_POKEMON_POST.subscribe { (bobber, action, stack, pokemonEntity) ->
            action.spawnablePosition.cause.entity?.let {
                if (it is ServerPlayer) it.catalogueDetailId(pokemonEntity.pokemon)
            }
        }
    }

    fun ServerPlayer.catalogueDetailId(pokemon: Pokemon) {
        SpawnDetailIdPropertyType.extract(pokemon)?.let { id ->
            SpawnDataCatalogue.executeAndSave(this) { data ->
                data.catalogue(id)
            }
        }
    }
}