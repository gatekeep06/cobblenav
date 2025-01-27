package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.feature.SeasonFeatureHandler
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object SpawnDataHelper {
    fun collect(
        detail: PokemonSpawnDetail,
        spawnChance: Float,
        contexts: List<AreaSpawningContext>,
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

        val conditions = mutableListOf<MutableComponent>()
        val blocks = mutableSetOf<ResourceLocation>()
        if (config.showPokemonTooltips && (!config.hideUnknownPokemonTooltips || encountered)) {
            val condition = detail.conditions.firstOrNull { contexts.any { context -> it.isSatisfiedBy(context) } }
            val fittingContexts = contexts.filter { condition?.isSatisfiedBy(it) == true }
            condition?.let {
                conditions += ConditionCollectors.collectConditions(it, fittingContexts, player)
                blocks += ConditionCollectors.collectBlockConditions(it, fittingContexts)
            }
        }

        return SpawnData(renderablePokemon, aspects, spawnChance, detail.context.name, encountered, conditions, BlockConditions(blocks))
    }
}