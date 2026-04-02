package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.api.pokefinder.RadarDotType
import com.metacontent.cobblenav.client.CobblenavClient

class EvYieldFilter(
    private var evYield: Set<Stats> = emptySet()
) : RadarFilter {
    companion object {
        const val TYPE = "ev"
    }

    override val type = TYPE

    override var dotType: RadarDotType? = null

    override fun test(pokemon: Pokemon): Boolean = pokemon.form.evYield
        .filter { it.value > 0 }.keys
        .containsAll(evYield)

    fun update(stats: Set<Stats>) {
        evYield = stats
        CobblenavClient.pokefinderSettings?.changed = true
    }

    fun get() = evYield
}