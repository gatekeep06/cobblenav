package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.feature.SeasonFeatureHandler
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.cobblemonResource
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.jvm.optionals.getOrNull

object SpawnDataHelper {
    fun collect(
        detail: PokemonSpawnDetail,
        spawnChance: Float,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): SpawnData? {
        val config = Cobblenav.config

        val renderablePokemon = detail.pokemon.let {
            val pokemon = Pokemon()
            it.apply(pokemon)
            SeasonFeatureHandler.updateSeason(pokemon, player.level(), player.onPos)
            pokemon.asRenderablePokemon()
        }

        val speciesRecord = detail.pokemon.species?.asIdentifierDefaultingNamespace()?.let {
            Cobblemon.playerDataManager.getPokedexData(player).getSpeciesRecord(it)
        }
        val encountered = speciesRecord?.hasSeenForm(renderablePokemon.form.name) ?: false

        if (!encountered && config.hideUnknownPokemon) {
            return null
        }

        val aspects = detail.pokemon.aspects

        var biome: ResourceLocation? = null
        val conditions = mutableListOf<MutableComponent>()
        val blocks = mutableSetOf<ResourceLocation>()
        if (config.showPokemonTooltips && (!config.hideUnknownPokemonTooltips || encountered)) {
            val condition = detail.conditions.firstOrNull { contexts.any { context -> it.isSatisfiedBy(context) } }
            val fittingContexts = contexts.filter { condition?.isSatisfiedBy(it) == true }
            condition?.let {
                biome = it.biomes
                    ?.mapNotNull { registryLikeCondition -> registryLikeCondition.toResourceLocation() }
                    ?.filter {
                        contexts.any { context ->
                            val registry = context.biomeRegistry
                            val biomeLocation = registry.getKey(context.biome) ?: return@any false
                            val biomeTags = registry.getHolder(biomeLocation).getOrNull()
                                ?.tags()?.map { tagKey -> tagKey.location }?.toList() ?: return@any false
                            return@any biomeLocation == it || biomeTags.contains(it)
                        }
                    }
                    ?.firstOrNull()
                val habitat = Component.translatable("gui.cobblenav.spawn_data.habitat")
                if (habitat.siblings.isEmpty()) habitat.append(
                    Component.translatable((biome ?: cobblemonResource("is_overworld")).toLanguageKey("biome"))
                )
                conditions += habitat
                conditions += ConditionCollectors.collectConditions(it, fittingContexts, player)
                blocks += ConditionCollectors.collectBlockConditions(
                    it,
                    fittingContexts.filterIsInstance<AreaSpawningContext>()
                )
            }
        }

        return SpawnData(
            renderablePokemon,
            aspects,
            spawnChance,
            biome,
            detail.context.name,
            encountered,
            conditions,
            BlockConditions(blocks)
        )
    }
}