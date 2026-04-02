package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.api.pokefinder.RadarDotType

interface RadarFilter {
    val type: String

    var dotType: RadarDotType?

    fun test(pokemon: Pokemon): Boolean
}