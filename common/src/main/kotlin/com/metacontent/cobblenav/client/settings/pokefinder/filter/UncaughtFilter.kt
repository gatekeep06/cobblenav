package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation

class UncaughtFilter : RadarFilter {
    companion object {
        const val TYPE = "uncaught"
    }

    override val type = TYPE

    override var dot: ResourceLocation? = null

    override fun test(pokemon: Pokemon): Boolean {
        val speciesRecord = CobblemonClient.clientPokedexData.getSpeciesRecord(pokemon.species.resourceIdentifier)
        val knowledge = speciesRecord?.getFormRecord(pokemon.form.name)?.knowledge ?: return true
        return knowledge != PokedexEntryProgress.CAUGHT
    }
}