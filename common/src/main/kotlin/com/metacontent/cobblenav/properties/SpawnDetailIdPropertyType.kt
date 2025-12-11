package com.metacontent.cobblenav.properties

import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.properties.StringProperty

object SpawnDetailIdPropertyType : CustomPokemonPropertyType<StringProperty> {
    const val BASE_KEY = "cobblenavdetailid"

    override val keys = setOf(BASE_KEY, "detailid")
    override val needsKey = true

    override fun fromString(value: String?) = value?.let { id ->
        StringProperty(
            key = BASE_KEY,
            value = id,
            applicator = { pokemon, value -> apply(pokemon, value) },
            matcher = { pokemon, value -> extract(pokemon) == value }
        )
    }

    override fun examples() = emptySet<String>()

    @JvmStatic
    fun extract(pokemon: Pokemon) = pokemon.persistentData.takeIf { it.contains(BASE_KEY) }?.getString(BASE_KEY)

    @JvmStatic
    fun extract(pokemonEntity: PokemonEntity) = extract(pokemonEntity.pokemon)

    @JvmStatic
    fun apply(pokemon: Pokemon, value: String) {
        pokemon.persistentData.putString(BASE_KEY, value)
    }

    @JvmStatic
    fun apply(pokemonEntity: PokemonEntity, value: String) {
        apply(pokemonEntity.pokemon, value)
    }
}