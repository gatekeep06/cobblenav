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
import java.text.DateFormat
import java.util.Date
import java.util.UUID

data class PokenavContact(
    val uuid: UUID,
    val name: String,
    val battleRecords: List<BattleRecord>,
    val date: Date = Date()
) {
    companion object {
        val CODEC: Codec<PokenavContact> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                PrimitiveCodec.STRING.fieldOf("name").forGetter { it.name },
                BattleRecord.CODEC.listOf().fieldOf("battleRecords").forGetter { it.battleRecords },
                PrimitiveCodec.LONG.fieldOf("date").forGetter { it.date.time }
            ).apply(instance) { uuid, name, battleRecords, date ->
                PokenavContact(UUID.fromString(uuid), name, battleRecords, Date(date))
            }
        }
    }

    fun toClientContact(): ClientPokenavContact {
        val profile = Cobblemon.playerDataManager.getProfileData(uuid)
        return ClientPokenavContact(
            uuid = uuid,
            name = name,
            titleId = profile.titleId,
            partnerPokemon = profile.partnerPokemonCache?.asRenderablePokemon(),
            battleRecords = battleRecords,
            date = date
        )
    }

    fun getSummary(): String {
        val stats = getBattleStats()
        return "$name (${DateFormat.getDateInstance().format(date)}) w: ${stats.wins} l: ${stats.losses} a: ${stats.ally}"
    }

    fun getBattleStats(): BattleStats {
        var wins = 0
        var losses = 0
        var ally = 0
        battleRecords.forEach {
            when (it.type) {
                RecordType.WIN -> wins++
                RecordType.LOSS -> losses++
                RecordType.ALLY -> ally++
            }
        }
        return BattleStats(wins, losses, ally)
    }

    data class BattleStats(
        val wins: Int,
        val losses: Int,
        val ally: Int
    )
}

data class ClientPokenavContact(
    val uuid: UUID,
    val name: String,
    val titleId: ResourceLocation?,
    val partnerPokemon: RenderablePokemon?,
    val battleRecords: List<BattleRecord>,
    val date: Date
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = ClientPokenavContact(
            uuid = buffer.readUUID(),
            name = buffer.readString(),
            titleId = buffer.readNullable { it.readResourceLocation() },
            partnerPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(buffer) },
            battleRecords = buffer.readList { BattleRecord.decode(it as RegistryFriendlyByteBuf) },
            date = buffer.readDate()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeUUID(uuid)
        buffer.writeString(name)
        buffer.writeNullable(titleId) { pb, value -> pb.writeResourceLocation(value) }
        buffer.writeNullable(partnerPokemon) { pb, value -> value.saveToBuffer(pb as RegistryFriendlyByteBuf) }
        buffer.writeCollection(battleRecords) { pb, value -> value.encode(pb as RegistryFriendlyByteBuf) }
        buffer.writeDate(date)
    }
}
