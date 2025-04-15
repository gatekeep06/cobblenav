package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.ListCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.util.StringRepresentable
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class BattleRecord(
    val participants: Map<String, List<PartyMember>>,
    val type: RecordType,
    val date: Date = Date()
) : Encodable {
    companion object {
        val CODEC: Codec<BattleRecord> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.unboundedMap(PrimitiveCodec.STRING, ListCodec(PartyMember.CODEC, 0, 512)).fieldOf("participants").forGetter { it.participants },
                RecordType.CODEC.fieldOf("type").forGetter { it.type },
                PrimitiveCodec.LONG.fieldOf("date").forGetter { it.date.time }
            ).apply(instance) { participants, type, date ->
                BattleRecord(participants, type, Date(date))
            }
        }

        fun decode(buffer: RegistryFriendlyByteBuf) = BattleRecord(
            participants = buffer.readMap(
                { it.readString() },
                { it.readList { b -> PartyMember.decode(b as RegistryFriendlyByteBuf) } }
            ),
            type = buffer.readEnum(RecordType::class.java),
            date = buffer.readDate()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeMap(
            participants,
            { pb, key -> pb.writeString(key) },
            { pb, value -> pb.writeCollection(value) { pb1, value1 -> value1.encode(pb1 as RegistryFriendlyByteBuf) } }
        )
        buffer.writeEnum(type)
        buffer.writeDate(date)
    }
}

enum class RecordType(private val serializedName: String) : StringRepresentable {
    WIN("win"),
    LOSS("loss"),
    ALLY("ally");

    override fun getSerializedName(): String = serializedName

    companion object {
        val CODEC: Codec<RecordType> = StringRepresentable.fromEnum(RecordType::values)
    }
}

data class PartyMember(
    val nickname: String?,
    val pokemon: String,
    val level: Int
) : Encodable {
    companion object {
        val CODEC: Codec<PartyMember> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.optionalFieldOf("nickname").forGetter { Optional.ofNullable(it.nickname) },
                PrimitiveCodec.STRING.fieldOf("pokemon").forGetter { it.pokemon },
                PrimitiveCodec.INT.fieldOf("level").forGetter { it.level }
            ).apply(instance) { nickname, pokemon, level ->
                PartyMember(nickname.getOrNull(), pokemon, level)
            }
        }

        fun decode(buffer: RegistryFriendlyByteBuf) = PartyMember(
            nickname = buffer.readNullable { it.readString() },
            pokemon = buffer.readString(),
            level = buffer.readInt()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeNullable(nickname) { pb, value -> pb.writeString(value) }
        buffer.writeString(pokemon)
        buffer.writeInt(level)
    }
}
