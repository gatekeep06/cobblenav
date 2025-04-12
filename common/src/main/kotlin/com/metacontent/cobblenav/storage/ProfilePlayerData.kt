package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.metacontent.cobblenav.api.contact.title.TrainerTitles
import net.minecraft.resources.ResourceLocation
import java.util.*

data class ProfilePlayerData(
    override val uuid: UUID,
    var titleId: ResourceLocation?,
    val grantedTitles: MutableSet<ResourceLocation>,
    var partnerPokemonUuid: UUID?,
    var partnerPokemonCache: PokemonProperties?
) : InstancedPlayerData {
    override fun toClientData(): ClientInstancedPlayerData {
        return ClientProfilePlayerData(
            title = titleId?.let { TrainerTitles.getTitle(it) },
            allowedTitles = TrainerTitles.getAllowed(grantedTitles),
            partnerPokemonUuid = partnerPokemonUuid
        )
    }
}