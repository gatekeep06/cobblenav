package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.api.pokefinder.RadarDotType

class ShinyFilter : RadarFilter {
    companion object {
        const val TYPE = "shiny"
    }

    override val type = TYPE

    override var dotType: RadarDotType? = null

    override fun test(pokemon: Pokemon): Boolean = pokemon.shiny
}