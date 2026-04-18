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

    private val advancedFilters = mutableListOf<RadarFilter>()

    fun getFilters(): List<RadarFilter> = advancedFilters.toList()

    fun addFilter(filter: RadarFilter) {
        changed = true
        advancedFilters.add(filter)
    }

    fun removeFilter(filter: RadarFilter) {
        changed = true
        advancedFilters.remove(filter)
    }

    fun clearFilters() {
        changed = true
        advancedFilters.clear()
    }

    fun test(pokemon: Pokemon): Boolean {
        return when (mode) {
            Mode.SIMPLE -> simpleNameFilter.test(pokemon)
                    && simpleAspectFilter.test(pokemon)
                    && simpleLabelFilter.test(pokemon)
                    && simpleShinyFilter.test(pokemon)

            Mode.ADVANCED -> advancedFilters.any { it.test(pokemon) } || advancedFilters.isEmpty()
            else -> true
        }
    }

    enum class Mode {
        SIMPLE,
        ADVANCED
    }
}