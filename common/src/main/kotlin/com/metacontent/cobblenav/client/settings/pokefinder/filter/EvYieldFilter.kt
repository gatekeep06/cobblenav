package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.CobblenavClient
import net.minecraft.resources.ResourceLocation

class EvYieldFilter(
    private var evYield: Set<Stats> = emptySet()
) : RadarFilter {
    companion object {
        const val TYPE = "ev"
    }

    override val type = TYPE

    override var dot: ResourceLocation? = null

    override fun test(pokemon: Pokemon): Boolean = pokemon.form.evYield
        .filter { it.value > 0 }.keys
        .containsAll(evYield)

    fun update(stats: Set<Stats>) {
        evYield = stats
        CobblenavClient.pokefinderSettings?.changed = true
    }

    fun get() = evYield
}