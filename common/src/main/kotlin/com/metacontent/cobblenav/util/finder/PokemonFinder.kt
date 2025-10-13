package com.metacontent.cobblenav.util.finder

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.targeting.TargetingConditions

abstract class PokemonFinder {
    companion object {
        val NO_EGG_MOVE: Component = Component.translatable("gui.cobblenav.finder.no_egg_move")
    }

    abstract fun select(
        pokemonEntities: List<PokemonEntity>,
        player: ServerPlayer,
        serverLevel: ServerLevel = player.serverLevel()
    ): FoundPokemon

    protected fun selectNearest(
        pokemonEntities: Collection<PokemonEntity>,
        player: ServerPlayer,
        level: ServerLevel = player.serverLevel()
    ): PokemonEntity? {
        val width = Cobblenav.config.searchAreaWidth
        val height = Cobblenav.config.searchAreaHeight
        return level.getNearestEntity(
            pokemonEntities.toList(),
            TargetingConditions.forNonCombat(),
            player,
            width, height, width
        )
    }

    fun getEggMoveName(pokemon: Pokemon): Component? {
        val allMoves = (pokemon.moveSet.getMoves().map { it.template } + pokemon.benchedMoves.map { it.moveTemplate })
            .toSet()
        val notEggMoves = pokemon.form.moves.levelUpMoves.flatMap { it.value } + pokemon.form.moves.evolutionMoves
        val eggMoves = pokemon.form.moves.eggMoves
        val eggMove = allMoves.firstOrNull { eggMoves.contains(it) && !notEggMoves.contains(it) }
        eggMove?.let { return it.displayName }
        return null
    }

    fun getPerfectIvsAmount(pokemon: Pokemon): Int = pokemon.ivs.count { it.value == IVs.MAX_VALUE }

    fun hasHiddenAbility(pokemon: Pokemon) = pokemon.form.abilities.mapping
        .flatMap { it.value }
        .find { it.template == pokemon.ability.template }
        ?.type == HiddenAbilityType
}