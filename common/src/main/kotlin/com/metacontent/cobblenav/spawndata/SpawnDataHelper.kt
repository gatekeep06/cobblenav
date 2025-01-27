package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.spawning.condition.*
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.feature.SeasonFeatureHandler
import com.cobblemon.mod.common.registry.BlockIdentifierCondition
import com.cobblemon.mod.common.registry.BlockTagCondition
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Block

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
        if (config.showPokemonTooltips && (config.showUnknownPokemonTooltips || encountered)) {
            val condition = detail.conditions.firstOrNull { contexts.any { context -> it.isSatisfiedBy(context) } }
            val fittingContexts = contexts.filter { condition?.isSatisfiedBy(it) == true }

            condition?.let {
                if (it is GroundedTypeSpawningCondition<*>) {
                    blocks += getInfluencedBlocks(it, fittingContexts)
                }
                if (it is SeafloorTypeSpawningCondition<*>) {
                    blocks += getInfluencedBlocks(it, fittingContexts)
                }
                if (it is AreaTypeSpawningCondition<*>) {
                    blocks += getInfluencedBlocks(it, fittingContexts)
                }
                conditions += ConditionCollectors.collect(it, fittingContexts, player)
            }
        }

        return SpawnData(renderablePokemon, aspects, spawnChance, detail.context.name, encountered, conditions, BlockConditions(blocks))
    }

    private fun getInfluencedBlocks(
        condition: AreaTypeSpawningCondition<*>,
        contexts: List<AreaSpawningContext>
    ): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededNearbyBlocks?.toBlockList() ?: emptyList()
        contexts.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }

    private fun getInfluencedBlocks(
        condition: GroundedTypeSpawningCondition<*>,
        contexts: List<AreaSpawningContext>
    ): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededBaseBlocks?.toBlockList() ?: emptyList()
        contexts.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }

    private fun getInfluencedBlocks(
        condition: SeafloorTypeSpawningCondition<*>,
        contexts: List<AreaSpawningContext>
    ): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededBaseBlocks?.toBlockList() ?: emptyList()
        contexts.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }

    private fun MutableList<RegistryLikeCondition<Block>>.toBlockList(): List<ResourceLocation> {
        return this.flatMap {
            if (it is BlockIdentifierCondition) {
                return@flatMap listOf(it.identifier)
            }
            if (it is BlockTagCondition) {
                if (it.tag.location.path == "natural") return@flatMap emptyList()
                val optional = BuiltInRegistries.BLOCK.getTag(it.tag)
                if (optional.isPresent) {
                    return@flatMap optional.get().map { blockHolder -> BuiltInRegistries.BLOCK.getKey(blockHolder.value()) }
                }
            }
            return@flatMap emptyList()
        }
    }
}