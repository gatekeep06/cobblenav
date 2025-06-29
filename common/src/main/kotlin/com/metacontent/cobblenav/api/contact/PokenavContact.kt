package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import java.util.*

data class PokenavContact(
    val id: String,
    val type: ContactType,
    val name: String,
    val date: Date = Date()
) {
    companion object {
        val CODEC: Codec<PokenavContact> = RecordCodecBuilder.create<PokenavContact> { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("id").forGetter { it.id },
                ContactType.CODEC.fieldOf("type").forGetter { it.type },
                PrimitiveCodec.STRING.fieldOf("name").forGetter { it.name },
                PrimitiveCodec.LONG.fieldOf("date").forGetter { it.date.time }
            ).apply(instance) { id, type, name, date ->
                PokenavContact(id, type, name, Date(date))
            }
        }
    }

    fun toClientContact(): ClientPokenavContact {
        val profile = type.profileDataExtractor(id)
        return ClientPokenavContact(
            id = id,
            name = name,
            titleId = profile.titleId,
            partnerPokemon = profile.partnerPokemon?.asRenderablePokemon(),
            date = date
        )
    }

//    fun summarizeBattles(): String = battles.values
//        .groupBy { it.result }
//        .map { "${it.key.name}: ${it.value.size}" }
//        .joinToString()
}

data class ClientPokenavContact(
    val id: String,
    val name: String,
    val titleId: ResourceLocation?,
    val partnerPokemon: RenderablePokemon?,
    val date: Date
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = ClientPokenavContact(
            id = buffer.readString(),
            name = buffer.readString(),
            titleId = buffer.readNullable { it.readResourceLocation() },
            partnerPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(buffer) },
            date = buffer.readDate()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(id)
        buffer.writeString(name)
        buffer.writeNullable(titleId) { pb, value -> pb.writeResourceLocation(value) }
        buffer.writeNullable(partnerPokemon) { pb, value -> value.saveToBuffer(pb as RegistryFriendlyByteBuf) }
        buffer.writeDate(date)
    }
}

data class ContactProfileData(
    val titleId: ResourceLocation?,
    val partnerPokemon: PokemonProperties?
)
