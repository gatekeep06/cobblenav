package com.metacontent.cobblenav.client.settings.pokefinder

import com.metacontent.cobblenav.client.settings.pokefinder.filter.*
import com.metacontent.cobblenav.client.settings.pokefinder.type.*

object RadarFilterTypeRegistry {
    private val advancedTypes = mutableMapOf<String, RadarFilterType<out RadarFilter>>()

    fun register(type: String, filterType: RadarFilterType<out RadarFilter>) {
        advancedTypes[type] = filterType
    }

    fun get(type: String): RadarFilterType<out RadarFilter>? = advancedTypes[type]

    fun advancedTypes(): Iterable<RadarFilterType<out RadarFilter>> = advancedTypes.values

    init {
        register(TranslatedNameFilter.TYPE, TranslatedNameFilterType)
        register(PokemonPropertiesFilter.TYPE, PokemonPropertiesFilterType)
        register(LabelFilter.TYPE, LabelFilterType)
        register(EvYieldFilter.TYPE, EvYieldFilterType)
        register(UncaughtFilter.TYPE, UncaughtFilterType)
    }
}