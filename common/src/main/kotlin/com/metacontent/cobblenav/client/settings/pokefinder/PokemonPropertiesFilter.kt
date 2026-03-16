package com.metacontent.cobblenav.client.settings.pokefinder

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget

class PokemonPropertiesFilter(
    private var properties: PokemonProperties
) : EditableTextFilter() {
    companion object {
        const val TYPE = "properties"
    }

    override val type = "properties"

    override val widget by lazy {
        TextFieldWidget(
            x = 0,
            y = 0,
            width = RadarFilter.WIDGET_WIDTH,
            height = RadarFilter.WIDGET_HEIGHT,
            default = properties.originalString,
            textureSheet = FIELD,
            onChange = {}
        )
    }

    override fun test(pokemon: Pokemon): Boolean = properties.test(pokemon)

    override fun onFinishEditing() {
        properties = PokemonProperties.parse(widget.value)
    }

    private fun PokemonProperties.test(pokemon: Pokemon): Boolean {
        return if (level != null && pokemon.level != level) {
            false
        } else if (shiny != null && pokemon.shiny != shiny) {
            false
        } else if (gender != null && pokemon.gender != gender) {
            false
        } else if (species != null && pokemon.species.name != species) {
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
}