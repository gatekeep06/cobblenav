package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.ListCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.util.StringRepresentable
import java.util.*

data class BattleRecord(
    val id: BattleId,
    val winners: Map<String, List<PokemonProperties>>,
    val losers: Map<String, List<PokemonProperties>>,
    val type: RecordType
) : Encodable {
    companion object {
        val CODEC: Codec<BattleRecord> = RecordCodecBuilder.create { instance ->
            instance.group(
                BattleId.CODEC.fieldOf("id").forGetter { it.id },
                Codec.unboundedMap(PrimitiveCodec.STRING, ListCodec(PokemonProperties.CODEC, 0, 512)).fieldOf("winners")
                    .forGetter { it.winners },
                Codec.unboundedMap(PrimitiveCodec.STRING, ListCodec(PokemonProperties.CODEC, 0, 512)).fieldOf("losers")
                    .forGetter { it.losers },
                RecordType.CODEC.fieldOf("type").forGetter { it.type }
            ).apply(instance) { id, winners, losers, type ->
                BattleRecord(id, winners, losers, type)
            }
        }

        fun decode(buffer: RegistryFriendlyByteBuf) = BattleRecord(
            id = BattleId.decode(buffer),
            winners = buffer.readMap(
                { it.readString() },
                { it.readList { b -> PokemonProperties.parse(b.readString()) } }
            ),
            losers = buffer.readMap(
                { it.readString() },
                { it.readList { b -> PokemonProperties.parse(b.readString()) } }
            ),
            type = buffer.readEnum(RecordType::class.java)
        )

        fun List<BattleActor>.toParticipants() = this.flatMap { actor ->
            actor.getPlayerUUIDs().map { uuid ->
                uuid.toString() to actor.pokemonList.map {
                    it.originalPokemon.createPokemonProperties(
                        PokemonPropertyExtractor.SPECIES,
                        PokemonPropertyExtractor.FORM,
                        PokemonPropertyExtractor.SHINY,
                        PokemonPropertyExtractor.ASPECTS,
                        PokemonPropertyExtractor.LEVEL,
                        PokemonPropertyExtractor.GENDER
                    )
                }
            }
        }.toMap()
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        id.encode(buffer)
        buffer.writeMap(
            winners,
            { pb, key -> pb.writeString(key) },
            { pb, value -> pb.writeCollection(value) { pb1, value1 -> pb1.writeString(value1.asString()) } }
        )
        buffer.writeMap(
            losers,
            { pb, key -> pb.writeString(key) },
            { pb, value -> pb.writeCollection(value) { pb1, value1 -> pb1.writeString(value1.asString()) } }
        )
        buffer.writeEnum(type)
    }
}

enum class RecordType : StringRepresentable {
    WIN,
    LOSS;

    override fun getSerializedName(): String = this.name

    companion object {
        val CODEC: Codec<RecordType> = StringRepresentable.fromEnum(RecordType::values)
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

//data class PartyMember(
//    val nickname: String?,
//    val pokemon: PokemonProperties,
//    val level: Int
//) : Encodable {
//    companion object {
//        val CODEC: Codec<PartyMember> = RecordCodecBuilder.create { instance ->
//            instance.group(
//                PrimitiveCodec.STRING.optionalFieldOf("nickname").forGetter { Optional.ofNullable(it.nickname) },
//                PokemonProperties.CODEC.fieldOf("pokemon").forGetter { it.pokemon },
//                PrimitiveCodec.INT.fieldOf("level").forGetter { it.level }
//            ).apply(instance) { nickname, pokemon, level ->
//                PartyMember(nickname.getOrNull(), pokemon, level)
//            }
//        }
//
//        fun decode(buffer: RegistryFriendlyByteBuf) = PartyMember(
//            nickname = buffer.readNullable { it.readString() },
//            pokemon = PokemonProperties.parse(buffer.readString()),
//            level = buffer.readInt()
//        )
//    }
//
//    override fun encode(buffer: RegistryFriendlyByteBuf) {
//        buffer.writeNullable(nickname) { pb, value -> pb.writeString(value) }
//        buffer.writeString(pokemon.asString())
//        buffer.writeInt(level)
//    }
//}
