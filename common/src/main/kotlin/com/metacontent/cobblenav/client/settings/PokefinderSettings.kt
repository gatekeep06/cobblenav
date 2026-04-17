package com.metacontent.cobblenav.client.settings

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.settings.pokefinder.filter.*

class PokefinderSettings : Settings<PokefinderSettings>() {
    companion object {
        const val NAME = "pokefinder"
    }

    @Transient
    override val name = NAME

    var mode: Mode? = null
        set(value) {
            changed = true
            field = value
        }

    val simpleNameFilter = TranslatedNameFilter()

    val simpleAspectFilter = AspectFilter()

    val simpleLabelFilter = LabelFilter()

    val simpleShinyFilter = ShinyFilter()

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
        return when (mode) {
            Mode.SIMPLE -> simpleNameFilter.test(pokemon)
                    && simpleAspectFilter.test(pokemon)
                    && simpleLabelFilter.test(pokemon)
                    && simpleShinyFilter.test(pokemon)

            Mode.ADVANCED -> filters.any { it.test(pokemon) } || filters.isEmpty()
            else -> true
        }
    }

    enum class Mode {
        SIMPLE,
        ADVANCED
    }
}