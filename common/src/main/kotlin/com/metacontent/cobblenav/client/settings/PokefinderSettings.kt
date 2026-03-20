package com.metacontent.cobblenav.client.settings

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter

class PokefinderSettings : Settings<PokefinderSettings>() {
    companion object {
        const val NAME = "pokefinder"
    }

    @Transient
    override val name = NAME

    private val filters = mutableListOf<RadarFilter>()

    fun getFilters(): List<RadarFilter> = filters.toList()

    fun addFilter(filter: RadarFilter) {
        changed = true
        filters.add(filter)
    }

    fun removeFilter(filter: RadarFilter) {
        changed = true
        filters.remove(filter)
    }

    fun clearFilters() {
        changed = true
        filters.clear()
    }

    fun test(pokemon: Pokemon): Boolean {
        return filters.any { it.test(pokemon) }
    }
}