package com.metacontent.cobblenav.client.settings.pokefinder

object RadarFilterRegistry {
    private val types = mutableMapOf<String, Class<out RadarFilter>>()

    fun register(type: String, clazz: Class<out RadarFilter>) {
        types[type] = clazz
    }

    fun get(type: String): Class<out RadarFilter>? = types[type]

    init {
        register(TranslatedNameFilter.TYPE, TranslatedNameFilter::class.java)
        register(PokemonPropertiesFilter.TYPE, PokemonPropertiesFilter::class.java)
    }
}