package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.settings.pokefinder.filter.EditableTextFilter

class PokemonPropertiesFilter(
    private var properties: PokemonProperties = PokemonProperties()
) : EditableTextFilter() {
    companion object {
        const val TYPE = "properties"
    }

    override val type = "properties"

    override fun test(pokemon: Pokemon): Boolean = properties.test(pokemon)

    private fun PokemonProperties.test(pokemon: Pokemon): Boolean {
        return if (level != null && pokemon.level != level) {
            false
        } else if (shiny != null && pokemon.shiny != shiny) {
            false
        } else if (gender != null && pokemon.gender != gender) {
            false
        } else if (species != null && !pokemon.species.name.equals(species, true)) {
            false
        } else if (form != null && !pokemon.form.name.equals(form, true)) {
            false
        } else if (type != null && pokemon.types.none { it.name.equals(type, true) }) {
            false
        } else if (scaleModifier != null && pokemon.scaleModifier != scaleModifier) {
            false
        } else if (!pokemon.aspects.containsAll(aspects)) {
            false
        } else {
            true
        }
    }

    override fun update(value: String) {
        properties = PokemonProperties.parse(value)
    }

    override fun asString(): String = properties.originalString
}