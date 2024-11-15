package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.registry.BiomeIdentifierCondition
import com.cobblemon.mod.common.registry.BiomeTagCondition
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object SpawnDataHelper {
    private const val FLYING_BIOME_CONDITION = "is_sky"
    private const val SWIMMING_CONTEXT_CONDITION = "submerged"
    private const val CLEAR_KEY = "clear"
    private const val RAIN_KEY = "rain"
    private const val THUNDER_KEY = "thunder"

    fun collect(
        detail: PokemonSpawnDetail,
        spawnChance: Float,
        contexts: List<AreaSpawningContext>,
        player: ServerPlayer
    ): SpawnData {
        val renderablePokemon = detail.pokemon.asRenderablePokemon()

        val speciesRecord = Cobblemon.playerDataManager.getPokedexData(player)
            .getSpeciesRecord(renderablePokemon.species.resourceIdentifier)
        val encountered = speciesRecord?.hasSeenForm(renderablePokemon.form.name) ?: false

        val contextBiomes = contexts.map { it.biomeName }.toMutableList()
        contexts.map { it.biomeRegistry.getHolder(it.biomeName) }
            .filter { it.isPresent }
            .flatMap { optional -> optional.get().tags().map { it.location }.toList() }
            .let { contextBiomes.addAll(it) }

        val biomeConditions = detail.conditions.flatMap { it.biomes ?: mutableSetOf() }
        val biomes = biomeConditions
            .filterIsInstance<BiomeIdentifierCondition>()
            .map { it.identifier }
            .toMutableList()
        biomeConditions
            .filterIsInstance<BiomeTagCondition>()
            .map { it.tag.location }
            .let { biomes.addAll(it) }
        val biome = biomes.firstOrNull { contextBiomes.contains(it) } ?: cobblemonResource("is_overworld")

        val condition = detail.conditions.firstOrNull { cond ->
            cond.biomes?.any { b ->
                b is BiomeTagCondition && b.tag.location == biome
            } ?: false
        }

        val additionalConditions = mutableSetOf<String>()
        val neededBlocks = mutableSetOf<ResourceLocation>()
        var time = IntRange(0, 23999)
        condition?.let {
            // TODO: check isThundering
            if (condition.isThundering == true) additionalConditions.add(THUNDER_KEY)
            else if (condition.isRaining == true) additionalConditions.add(RAIN_KEY)
            if (condition.isRaining == false) additionalConditions.add(CLEAR_KEY)
            condition.moonPhase?.ranges?.any { it.contains(player.level().moonPhase) }?.let {
                if (it) {
                    additionalConditions.add(MoonPhase.ofWorld(player.level()).name.lowercase())
                }
            }
            condition.timeRange?.ranges?.firstOrNull { it.contains(player.level().dayTime % 23999) }?.let {
                time = it
            }
        }

        var pose = PoseType.PROFILE
        if (detail.context.name == SWIMMING_CONTEXT_CONDITION) {
            pose = PoseType.SWIM
        }
        if (biome.path == FLYING_BIOME_CONDITION) {
            pose = PoseType.FLY
        }

        return SpawnData(renderablePokemon, spawnChance, encountered, biome, time, additionalConditions, neededBlocks, pose)
    }
}