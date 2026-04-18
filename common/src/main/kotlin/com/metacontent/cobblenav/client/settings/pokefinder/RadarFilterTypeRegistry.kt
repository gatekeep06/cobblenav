package com.metacontent.cobblenav.client.settings.pokefinder

import com.metacontent.cobblenav.client.settings.pokefinder.filter.*
import com.metacontent.cobblenav.client.settings.pokefinder.type.*

object RadarFilterTypeRegistry {
    private val advancedFilters = mutableMapOf<String, RadarFilterType<out RadarFilter>>()

    fun register(type: String, filterType: RadarFilterType<out RadarFilter>) {
        advancedFilters[type] = filterType
    }

    fun get(type: String): RadarFilterType<out RadarFilter>? = advancedFilters[type]

    fun types(): Iterable<RadarFilterType<out RadarFilter>> = advancedFilters.values

    init {
        register(TranslatedNameFilter.TYPE, TranslatedNameFilterType)
        register(PokemonPropertiesFilter.TYPE, PokemonPropertiesFilterType)
        register(LabelFilter.TYPE, LabelFilterType)
        register(EvYieldFilter.TYPE, EvYieldFilterType)
        register(UncaughtFilter.TYPE, UncaughtFilterType)
    }
}