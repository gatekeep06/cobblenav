package com.metacontent.cobblenav.event

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer

data class PokemonEncounteredEvent(
    val pokemon: Pokemon,
    val player: ServerPlayer?
)
