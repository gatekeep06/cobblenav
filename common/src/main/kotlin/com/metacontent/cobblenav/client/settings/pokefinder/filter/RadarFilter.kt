package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon

interface RadarFilter {
    val type: String

    fun test(pokemon: Pokemon): Boolean
}