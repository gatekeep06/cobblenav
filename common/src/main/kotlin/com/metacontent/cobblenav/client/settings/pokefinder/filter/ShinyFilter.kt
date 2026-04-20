package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.CobblenavClient

class ShinyFilter(
    private var enabled: Boolean = false
) : RadarFilter {
    companion object {
        const val TYPE = "shiny"
    }

    override val type = TYPE

    override fun test(pokemon: Pokemon): Boolean = !enabled || pokemon.shiny

    fun get(): Boolean = enabled

    fun toggle(): Boolean {
        enabled = !enabled
        CobblenavClient.pokefinderSettings?.changed = true
        return enabled
    }

    override fun clear() {
        enabled = false
    }
}