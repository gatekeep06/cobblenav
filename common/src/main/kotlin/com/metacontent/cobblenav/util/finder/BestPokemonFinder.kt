package com.metacontent.cobblenav.util.finder

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import kotlin.math.max

object BestPokemonFinder : PokemonFinder() {
    override fun select(pokemonEntities: List<PokemonEntity>, player: ServerPlayer, level: ServerLevel): FoundPokemon {
        val weights = Cobblenav.config.pokemonFeatureWeights
        val entityToFoundPokemon = mutableMapOf<PokemonEntity, FoundPokemon>()
        var maxRating = 0f

        pokemonEntities.forEach { entity ->
            val pokemon = entity.pokemon
            val builder = FoundPokemon.Builder()
                .found(true)
                .entityId(entity.id)
                .aspects(pokemon.aspects)
                .level(pokemon.level)

            var rating = 0f

            if (pokemon.shiny) rating += weights.shiny

            builder.potentialStars(getPerfectIvsAmount(pokemon))
            rating += weights.perfectIvsRates.getOrDefault(builder.potentialStars, 0f)

            builder.abilityHidden(hasHiddenAbility(pokemon))
            if (builder.isAbilityHidden) {
                rating += weights.hiddenAbility
            }
            builder.ability(Component.translatable(pokemon.ability.displayName))

            builder.eggMove(getEggMoveName(pokemon)?.also { rating += weights.eggMove }
                ?: NO_EGG_MOVE)

            maxRating = max(maxRating, rating)
            builder.rating(rating)
            entityToFoundPokemon[entity] = builder.build()
        }

        val entity = selectNearest(entityToFoundPokemon.filter { it.value.rating >= maxRating }.keys, player, level)
        return entityToFoundPokemon[entity] ?: FoundPokemon.NOT_FOUND
    }
}