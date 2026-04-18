package com.metacontent.cobblenav.client.settings.pokefinder

import com.metacontent.cobblenav.client.settings.pokefinder.filter.*
import com.metacontent.cobblenav.client.settings.pokefinder.type.*

object RadarFilterTypeRegistry {
    private val types = mutableMapOf<String, RadarFilterType<out RadarFilter>>()
    private val simpleTypes = mutableListOf<RadarFilterType<out RadarFilter>>()
    private val advancedTypes = mutableListOf<RadarFilterType<out RadarFilter>>()

    fun register(type: String, filterType: RadarFilterType<out RadarFilter>, mode: Mode) {
        types[type] = filterType
        when (mode) {
            Mode.SIMPLE -> simpleTypes.add(filterType)
            Mode.ADVANCED -> advancedTypes.add(filterType)
            Mode.BOTH -> {
                simpleTypes.add(filterType)
                advancedTypes.add(filterType)
            }
        }
    }

    fun get(type: String): RadarFilterType<out RadarFilter>? = types[type]

    fun simpleTypes(): Iterable<RadarFilterType<out RadarFilter>> = simpleTypes.toList()

    fun advancedTypes(): Iterable<RadarFilterType<out RadarFilter>> = advancedTypes.toList()

    init {
        register(TranslatedNameFilter.TYPE, TranslatedNameFilterType, Mode.BOTH)
        register(AspectFilter.TYPE, AspectFilterType, Mode.SIMPLE)
        register(ShinyFilter.TYPE, ShinyFilterType, Mode.SIMPLE)
        register(PokemonPropertiesFilter.TYPE, PokemonPropertiesFilterType, Mode.ADVANCED)
        register(LabelFilter.TYPE, LabelFilterType, Mode.BOTH)
        register(EvYieldFilter.TYPE, EvYieldFilterType, Mode.ADVANCED)
        register(UncaughtFilter.TYPE, UncaughtFilterType, Mode.ADVANCED)
    }

    enum class Mode { SIMPLE, ADVANCED, BOTH }
}