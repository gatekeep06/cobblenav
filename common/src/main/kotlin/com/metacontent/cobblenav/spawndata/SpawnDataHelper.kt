package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.spawning.SpawnBucket
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnPool
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.properties.SpawnDetailIdPropertyType
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.spawndata.handler.FishingSpawnerCheckHandler
import com.metacontent.cobblenav.spawndata.handler.FixedAreaSpawnerCheckHandler
import com.metacontent.cobblenav.spawndata.handler.PlayerSpawnerCheckHandler
import com.metacontent.cobblenav.spawndata.resultdata.SpawnResultData
import com.metacontent.cobblenav.storage.SpawnDataCatalogue
import com.metacontent.cobblenav.util.WeightedBucket
import com.metacontent.cobblenav.util.spawnCatalogue
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object SpawnDataHelper {
    const val BASE_FISHING_POKEMON_CHANCE = 0.85f

    val playerSpawnChecker = PlayerSpawnerCheckHandler
    val fishingSpawnChecker = FishingSpawnerCheckHandler
    val fixedAreaSpawnChecker = FixedAreaSpawnerCheckHandler

    private lateinit var spawnDetails: Map<String, List<Pair<SpawnDetail, CompositeConditionData>>>
    val spawnDetailIds
        get() = spawnDetails.keys
    private lateinit var spawnDetailIdBySpecies: Map<String, List<String>>

    fun getSpawnDetailIds(species: String): List<String>? = spawnDetailIdBySpecies[species]

    fun calculateWeightedBuckets(
        bucketWeights: MutableMap<SpawnBucket, Float>,
        influences: List<SpawningInfluence>
    ): List<WeightedBucket> {
        influences.forEach { it.affectBucketWeights(bucketWeights) }
        val sum = bucketWeights.values.sum()
        return bucketWeights.map { (key, value) -> WeightedBucket(key.name, value / sum) }
    }

    fun getSpawnData(
        detail: SpawnDetail,
        player: ServerPlayer
    ): SpawnData? {
        val result = SpawnResultData.fromDetail(detail, player) ?: return null

        val isKnownSpawn =
            !Cobblenav.config.hideConditionsOfUnknownSpawns || player.spawnCatalogue().contains(detail)
        val compositeConditions = if (isKnownSpawn) {
            spawnDetails[detail.id]?.first { it.first == detail }?.second ?: run {
                Cobblenav.LOGGER.error("Something is off. I can feel it")
                return null
            }
        } else {
            val condition = ConditionData("unknown", 0xffffff, emptyList())
            CompositeConditionData(
                conditions = listOf(condition),
                anticonditions = listOf(condition),
                blockConditions = BlockConditions(mutableSetOf()),
                blockAnticonditions = BlockConditions(mutableSetOf())
            )
        }

        return SpawnData(
            id = if (!isKnownSpawn) detail.id else "???",
            result = result,
            positionType = detail.spawnablePositionType.name,
            bucket = detail.bucket.name,
            weight = detail.weight,
            compositeConditions = compositeConditions
        )
    }

    fun getSpawnData(
        detailId: String,
        player: ServerPlayer
    ): List<SpawnData> {
        val details = spawnDetails[detailId] ?: return emptyList()
        return details.mapNotNull { (detail, conditions) ->
            val result = SpawnResultData.fromDetail(detail, player) ?: return@mapNotNull null

            val isKnownSpawn =
                !Cobblenav.config.hideConditionsOfUnknownSpawns || player.spawnCatalogue().contains(detail)
            val finalConditions = if (isKnownSpawn) {
                conditions
            } else {
                val condition = ConditionData("unknown", 0xffffff, emptyList())
                CompositeConditionData(
                    conditions = listOf(condition),
                    anticonditions = listOf(condition),
                    blockConditions = BlockConditions(mutableSetOf()),
                    blockAnticonditions = BlockConditions(mutableSetOf())
                )
            }

            SpawnData(
                id = if (isKnownSpawn) detail.id else "???",
                result = result,
                positionType = detail.spawnablePositionType.name,
                bucket = detail.bucket.name,
                weight = detail.weight,
                compositeConditions = finalConditions
            )
        }
    }

    fun collectConditions(detail: SpawnDetail): CompositeConditionData {
        val conditions = mutableListOf<ConditionData>()
        val blockConditions = mutableSetOf<ResourceLocation>()
        val anticonditions = mutableListOf<ConditionData>()
        val blockAnticonditions = mutableSetOf<ResourceLocation>()
        detail.conditions.forEach { condition ->
            conditions += ConditionCollectors.collectConditions(condition)
            blockConditions += ConditionCollectors.collectBlockConditions(condition)
        }
        detail.anticonditions.forEach { condition ->
            anticonditions += ConditionCollectors.collectConditions(condition)
            blockAnticonditions += ConditionCollectors.collectBlockConditions(condition)
        }
        return CompositeConditionData(
            conditions = conditions,
            anticonditions = anticonditions,
            blockConditions = BlockConditions(blockConditions),
            blockAnticonditions = BlockConditions(blockAnticonditions)
        )
    }

    fun reloadSpawnDetails(spawnPool: SpawnPool) {
        spawnDetails = spawnPool.groupBy(SpawnDetail::id)
            .mapValues { entry -> entry.value.map { it to collectConditions(it) } }
        spawnDetailIdBySpecies = spawnPool.filterIsInstance<PokemonSpawnDetail>()
            .groupBy { it.pokemon.species ?: "none" }
            .mapValues { it.value.map(SpawnDetail::id) }
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