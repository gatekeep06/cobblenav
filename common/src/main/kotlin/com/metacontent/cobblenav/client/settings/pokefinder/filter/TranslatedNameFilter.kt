package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.settings.pokefinder.filter.EditableTextFilter

class TranslatedNameFilter(
    private var names: List<String>
) : EditableTextFilter() {
    companion object {
        const val TYPE = "name"
    }

    override val type = TYPE

    override fun test(pokemon: Pokemon): Boolean = names.any {
        it.equals(pokemon.getDisplayName().string, true)
    }

    override fun update(value: String) {
        names = value.split(",").map(String::trim)
    }

    override fun asString(): String = names.joinToString()
}