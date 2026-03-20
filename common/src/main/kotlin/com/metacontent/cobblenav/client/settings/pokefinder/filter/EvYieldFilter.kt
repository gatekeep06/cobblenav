package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon

class EvYieldFilter(
    private var evYield: Set<Stats>
) : RadarFilter {
    companion object {
        const val TYPE = "ev"
    }

    override val type = TYPE

    override fun test(pokemon: Pokemon): Boolean = pokemon.form.evYield
        .filter { it.value > 0 }.keys
        .containsAll(evYield)

    fun update(stats: Set<Stats>) {
        evYield = stats
    }
}