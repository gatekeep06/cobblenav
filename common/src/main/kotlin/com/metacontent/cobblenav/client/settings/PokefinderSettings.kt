package com.metacontent.cobblenav.client.settings

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.settings.pokefinder.RadarFilter

class PokefinderSettings : Settings<PokefinderSettings>() {
    companion object {
        const val NAME = "pokefinder"
    }

    @Transient
    override val name = NAME

    var filters = setOf<RadarFilter<*>>()
        set(value) {
            changed = true
            field = value
        }

    fun test(pokemon: Pokemon): Boolean {
        return filters.any { it.test(pokemon) }
    }
}