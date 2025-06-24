package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.util.StringRepresentable
import java.util.*

data class ContactBattleRecord(
    val id: BattleId,
    val team: List<PokemonProperties>,
    val result: BattleResult
) : Encodable {
    companion object {
        val CODEC: Codec<ContactBattleRecord> = RecordCodecBuilder.create { instance ->
            instance.group(
                BattleId.CODEC.fieldOf("id").forGetter { it.id },
                PokemonProperties.CODEC.listOf().fieldOf("team").forGetter { it.team },
                BattleResult.CODEC.fieldOf("result").forGetter { it.result }
            ).apply(instance) { id, team, result ->
                ContactBattleRecord(id, team, result)
            }
        }

        val EXTRACTORS = mutableListOf(
            PokemonPropertyExtractor.SPECIES,
            PokemonPropertyExtractor.NICKNAME,
            PokemonPropertyExtractor.FORM,
            PokemonPropertyExtractor.SHINY,
            PokemonPropertyExtractor.ASPECTS,
            PokemonPropertyExtractor.LEVEL,
            PokemonPropertyExtractor.GENDER
        )

        fun decode(buffer: RegistryFriendlyByteBuf) = ContactBattleRecord(
            id = BattleId.decode(buffer),
            team = buffer.readList { PokemonProperties.parse(buffer.readString()) },
            result = buffer.readEnum(BattleResult::class.java)
        )

        fun BattleActor.getBattleRecord(id: BattleId, contactReceiver: BattleActor, winners: Iterable<BattleActor>) =
            ContactBattleRecord(
                id = id,
                team = pokemonList.mapNotNull {
                    if (it.facedOpponents.isNotEmpty()) {
                        it.originalPokemon.createPokemonProperties(EXTRACTORS)
                    } else {
                        null
                    }
                },
                result = if (this.getSide().actors.contains(contactReceiver)) {
                    BattleResult.ALLY
                } else if (winners.contains(this)) {
                    BattleResult.LOSS
                } else {
                    BattleResult.WIN
                }
            )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        id.encode(buffer)
        buffer.writeCollection(team) { buf, properties -> buf.writeString(properties.asString()) }
        buffer.writeEnum(result)
    }
}

enum class BattleResult : StringRepresentable {
    WIN,
    LOSS,
    ALLY;

    override fun getSerializedName(): String = this.name

    companion object {
        val CODEC: Codec<BattleResult> = StringRepresentable.fromEnum(BattleResult::values)
    }
}

data class BattleId(
    val uuid: UUID,
    val date: Date = Date()
) : Encodable {
    companion object {
        val CODEC: Codec<BattleId> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                PrimitiveCodec.LONG.fieldOf("date").forGetter { it.date.time }
            ).apply(instance) { uuid, date ->
                BattleId(UUID.fromString(uuid), Date(date))
            }
        }

        fun decode(buffer: RegistryFriendlyByteBuf) = BattleId(
            uuid = buffer.readUUID(),
            date = buffer.readDate()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeUUID(uuid)
        buffer.writeDate(date)
    }
}