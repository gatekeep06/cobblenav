package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.api.pokefinder.RadarDotType
import com.metacontent.cobblenav.client.CobblenavClient

class LabelFilter(
    private var labels: List<String> = emptyList()
) : EditableTextFilter() {
    companion object {
        const val TYPE = "label"
    }

    override val type = TYPE

    override var dotType: RadarDotType? = null

    override fun test(pokemon: Pokemon): Boolean = pokemon.form.labels.containsAll(labels)

    override fun update(value: String) {
        labels = value.split(",").map(String::trim)
        CobblenavClient.pokefinderSettings?.changed = true
    }

    override fun asString(): String = labels.joinToString()
}