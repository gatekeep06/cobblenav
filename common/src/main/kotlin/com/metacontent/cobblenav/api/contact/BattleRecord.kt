package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import net.minecraft.network.RegistryFriendlyByteBuf
import java.time.Instant
import java.util.*

data class BattleRecord(
    val participants: Map<String, List<PartyMember>>,
    val date: Date = Date.from(Instant.now())
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = BattleRecord(
            participants = buffer.readMap(
                { it.readString() },
                { it.readList { b -> PartyMember.decode(b as RegistryFriendlyByteBuf) } }
            ),
            date = buffer.readDate()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeMap(
            participants,
            { pb, key -> pb.writeString(key) },
            { pb, value -> pb.writeCollection(value) { pb1, value1 -> value1.encode(pb1 as RegistryFriendlyByteBuf) } }
        )
        buffer.writeDate(date)
    }
}

data class PartyMember(
    val nickname: String?,
    val pokemon: String,
    val level: Int
) : Encodable {
    companion object {
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
