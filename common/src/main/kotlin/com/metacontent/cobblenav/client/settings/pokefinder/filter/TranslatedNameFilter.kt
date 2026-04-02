package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.api.pokefinder.RadarDotType
import com.metacontent.cobblenav.client.CobblenavClient

class TranslatedNameFilter(
    private var names: List<String> = emptyList()
) : EditableTextFilter() {
    companion object {
        const val TYPE = "name"
    }

    override val type = TYPE

    override var dotType: RadarDotType? = null

    override fun test(pokemon: Pokemon): Boolean = names.any {
        it.equals(pokemon.getDisplayName().string, true)
    }

    override fun update(value: String) {
        names = value.split(",").map(String::trim)
        CobblenavClient.pokefinderSettings?.changed = true
    }

    override fun asString(): String = names.joinToString()
}