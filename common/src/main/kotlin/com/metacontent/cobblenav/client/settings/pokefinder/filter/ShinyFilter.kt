package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon

class ShinyFilter(
    val enabled: Boolean = false
) : RadarFilter {
    companion object {
        const val TYPE = "shiny"
    }

    override val type = TYPE

    override fun test(pokemon: Pokemon): Boolean = !enabled || pokemon.shiny
}