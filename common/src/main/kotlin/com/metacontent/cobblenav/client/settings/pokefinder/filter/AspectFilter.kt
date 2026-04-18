package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.CobblenavClient

class AspectFilter(
    private var aspects: Set<String> = emptySet()
) : EditableTextFilter() {
    companion object {
        const val TYPE = "aspect"
    }

    override val type = TYPE

    override fun test(pokemon: Pokemon): Boolean = pokemon.aspects.containsAll(aspects)

    override fun update(value: String) {
        aspects = value.split(",").map(String::trim).toSet()
        CobblenavClient.pokefinderSettings?.changed = true
    }

    override fun asString(): String = aspects.joinToString()

    override fun clear() {
        aspects = emptySet()
    }
}