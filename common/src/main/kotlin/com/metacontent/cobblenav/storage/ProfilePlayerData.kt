package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.util.asUUID
import com.metacontent.cobblenav.api.contact.title.TrainerTitles
import com.metacontent.cobblenav.storage.client.ClientProfilePlayerData
import com.mojang.serialization.codecs.ListCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class ProfilePlayerData(
    override val uuid: UUID,
    var titleId: ResourceLocation?,
    val grantedTitles: MutableSet<ResourceLocation>,
    var partnerPokemonUuid: UUID?,
    var partnerPokemonCache: PokemonProperties?
) : InstancedPlayerData {
    companion object {
        val CODEC = RecordCodecBuilder.create<ProfilePlayerData> { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                ResourceLocation.CODEC.optionalFieldOf("titleId").forGetter { Optional.ofNullable(it.titleId) },
                ListCodec(ResourceLocation.CODEC, 0, 512).fieldOf("grantedTitles").forGetter { it.grantedTitles.toList() },
                PrimitiveCodec.STRING.optionalFieldOf("partnerPokemonUuid").forGetter { Optional.ofNullable(it.partnerPokemonUuid?.toString()) },
                PokemonProperties.CODEC.optionalFieldOf("partnerPokemonCache").forGetter { Optional.ofNullable(it.partnerPokemonCache) }
            ).apply(instance) { uuid, titleId, grantedTitles, partnerPokemonUuid, partnerPokemonCache ->
                ProfilePlayerData(
                    uuid = UUID.fromString(uuid),
                    titleId = titleId.getOrNull(),
                    grantedTitles = grantedTitles.toMutableSet(),
                    partnerPokemonUuid = partnerPokemonUuid.getOrNull()?.let { UUID.fromString(it) },
                    partnerPokemonCache = partnerPokemonCache.getOrNull()
                )
            }
        }
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientProfilePlayerData(
            title = titleId?.let { TrainerTitles.getTitle(it) },
            allowedTitles = TrainerTitles.getAllowed(grantedTitles),
            partnerPokemonUuid = partnerPokemonUuid
        )
    }
}