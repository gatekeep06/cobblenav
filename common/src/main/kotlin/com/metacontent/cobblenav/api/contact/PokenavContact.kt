package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.util.getProfileData
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

data class PokenavContact(
    val contactId: ContactID,
    val name: String,
    val battleRecords: List<BattleRecord>
) {
    companion object {
        val CODEC: Codec<PokenavContact> = RecordCodecBuilder.create<PokenavContact> { instance ->
            instance.group(
                ContactID.CODEC.fieldOf("contactId").forGetter { it.contactId },
                PrimitiveCodec.STRING.fieldOf("name").forGetter { it.name },
                BattleRecord.CODEC.listOf().fieldOf("battleRecords").forGetter { it.battleRecords }
            ).apply(instance) { contactId, name, battleRecords ->
                PokenavContact(contactId, name, battleRecords)
            }
        }
    }

    fun toClientContact(): ClientPokenavContact {
        val profile = Cobblemon.playerDataManager.getProfileData(contactId.uuid)
        return ClientPokenavContact(
            contactID = contactId,
            name = name,
            titleId = profile.titleId,
            partnerPokemon = profile.partnerPokemonCache?.asRenderablePokemon(),
            battleRecords = battleRecords
        )
    }
}

data class ClientPokenavContact(
    val contactID: ContactID,
    val name: String,
    val titleId: ResourceLocation?,
    val partnerPokemon: RenderablePokemon?,
    val battleRecords: List<BattleRecord>
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = ClientPokenavContact(
            contactID = ContactID.decode(buffer),
            name = buffer.readString(),
            titleId = buffer.readNullable { it.readResourceLocation() },
            partnerPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(buffer) },
            battleRecords = buffer.readList { BattleRecord.decode(it as RegistryFriendlyByteBuf) }
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        contactID.encode(buffer)
        buffer.writeString(name)
        buffer.writeNullable(titleId) { pb, value -> pb.writeResourceLocation(value) }
        buffer.writeNullable(partnerPokemon) { pb, value -> value.saveToBuffer(pb as RegistryFriendlyByteBuf) }
        buffer.writeCollection(battleRecords) { pb, value -> value.encode(pb as RegistryFriendlyByteBuf) }
    }
}
