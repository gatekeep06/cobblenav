package com.metacontent.cobblenav.client.settings

import com.cobblemon.mod.common.pokemon.Pokemon

class PokefinderSettings : Settings<PokefinderSettings>() {
    companion object {
        const val NAME = "pokefinder"
    }

    @Transient
    override val name = NAME

    var species = setOf<String>()
        set(value) {
            changed = true
            field = value
        }
    var aspects = setOf<String>()
        set(value) {
            changed = true
            field = value
        }
    var labels = setOf<String>()
        set(value) {
            changed = true
            field = value
        }
    var strictAspectCheck = false
        set(value) {
            changed = true
            field = value
        }
    var strictLabelCheck = false
        set(value) {
            changed = true
            field = value
        }
    var shinyOnly = false
        set(value) {
            changed = true
            field = value
        }

    fun check(pokemon: Pokemon): Boolean {
        return if (species.isNotEmpty() && !species.map(String::lowercase).contains(pokemon.species.name.lowercase())) {
            false
        }
        else if (strictAspectCheck && !pokemon.aspects.containsAll(aspects.map(String::lowercase))) {
            false
        }
        else if (!strictAspectCheck && aspects.isNotEmpty() && !aspects.any { pokemon.aspects.contains(it.lowercase()) }) {
            false
        }
        else if (strictLabelCheck && !pokemon.form.labels.containsAll(labels.map(String::lowercase))) {
            false
        }
        else if (!strictLabelCheck && labels.isNotEmpty() && !labels.any { pokemon.form.labels.contains(it.lowercase()) }) {
            false
        }
        else if (shinyOnly && !pokemon.shiny) {
            false
        }
        else {
            true
        }
    }
}