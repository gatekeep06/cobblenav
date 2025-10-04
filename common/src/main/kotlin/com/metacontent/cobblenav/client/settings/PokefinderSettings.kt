package com.metacontent.cobblenav.client.settings

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon

class PokefinderSettings : Settings<PokefinderSettings>() {
    companion object {
        const val NAME = "pokefinder"
    }

    @Transient
    override val name = NAME

    val finderEntries = mutableListOf<PokemonProperties>()

    fun check(pokemon: Pokemon) = finderEntries.any { it.matches(pokemon) }

    fun addEntry(entry: PokemonProperties) {
        changed = true
        finderEntries.add(entry)
    }

    fun setEntry(index: Int, entry: PokemonProperties) {
        changed = true
        finderEntries[index] = entry
    }

    fun removeEntry(entry: PokemonProperties) {
        if (finderEntries.remove(entry)) {
            changed = true
        }
    }
}