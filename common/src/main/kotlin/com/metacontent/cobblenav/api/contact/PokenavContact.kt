package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.util.getProfileData
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

data class PokenavContact(
    val contactID: ContactID,
    val name: String,
    val battleRecords: List<BattleRecord>
) {
    fun toClientContact(): ClientPokenavContact {
        val profile = Cobblemon.playerDataManager.getProfileData(contactID.uuid)
        return ClientPokenavContact(
            contactID = contactID,
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
