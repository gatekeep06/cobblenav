package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation

class ShinyFilter : RadarFilter {
    companion object {
        const val TYPE = "shiny"
    }

    override val type = TYPE

    override var dot: ResourceLocation? = null

    override fun test(pokemon: Pokemon): Boolean = pokemon.shiny
}