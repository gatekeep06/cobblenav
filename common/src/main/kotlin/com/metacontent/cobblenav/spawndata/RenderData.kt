package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.pokemon.RenderablePokemon

interface RenderData {
    fun render()
}

class PokemonRenderData(
    val pokemon: RenderablePokemon
) : RenderData {
    override fun render() {
        TODO("Not yet implemented")
    }
}

class PokemonHerdRenderData(
    val leader: RenderablePokemon,
    val leftPokemon: RenderablePokemon,
    val rightPokemon: RenderablePokemon
) : RenderData {
    override fun render() {
        TODO("Not yet implemented")
    }
}