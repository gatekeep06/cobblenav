package com.metacontent.cobblenav.util.finder

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

object NearestPokemonFinder : PokemonFinder() {
    override fun select(pokemonEntities: List<PokemonEntity>, player: ServerPlayer, level: ServerLevel): FoundPokemon {
        val entity = selectNearest(pokemonEntities, player, level) ?: return FoundPokemon.NOT_FOUND
        val pokemon = entity.pokemon
        return FoundPokemon(
            found = true,
            entityId = entity.id,
            aspects = pokemon.aspects,
            level = pokemon.level,
            potentialStars = getPerfectIvsAmount(pokemon),
            ability = Component.translatable(pokemon.ability.displayName),
            isAbilityHidden = hasHiddenAbility(pokemon),
            eggMove = getEggMoveName(pokemon) ?: NO_EGG_MOVE,
            rating = 0f
        )
    }
}