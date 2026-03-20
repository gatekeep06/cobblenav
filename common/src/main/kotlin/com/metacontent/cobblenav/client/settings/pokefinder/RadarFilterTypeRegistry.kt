package com.metacontent.cobblenav.client.settings.pokefinder

import com.metacontent.cobblenav.client.settings.pokefinder.filter.PokemonPropertiesFilter
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.filter.TranslatedNameFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.PokemonPropertiesFilterType
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import com.metacontent.cobblenav.client.settings.pokefinder.type.TranslatedNameFilterType

object RadarFilterTypeRegistry {
    private val types = mutableMapOf<String, RadarFilterType<out RadarFilter>>()

    fun register(type: String, filterType: RadarFilterType<out RadarFilter>) {
        types[type] = filterType
    }

    fun get(type: String): RadarFilterType<out RadarFilter>? = types[type]

    init {
        register(TranslatedNameFilter.TYPE, TranslatedNameFilterType)
        register(PokemonPropertiesFilter.TYPE, PokemonPropertiesFilterType)
    }
}