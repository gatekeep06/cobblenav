package com.metacontent.cobblenav.api.contact.npc

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import net.minecraft.resources.ResourceLocation

data class NPCProfile(
    val id: ResourceLocation,
    val name: String?,
    val title: ResourceLocation?,
    val partnerPokemon: PokemonProperties?,
    val commonForAllEntities: Boolean,
    val postBattleContact: PostBattleContactProvider,
    val recordBattles: Boolean
)
