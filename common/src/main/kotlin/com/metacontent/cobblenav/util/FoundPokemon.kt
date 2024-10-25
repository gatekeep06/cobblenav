package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.*
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component

data class FoundPokemon(
    val found: Boolean,
    val entityId: Int,
    val aspects: Set<String>,
    val level: Int,
    val potentialStars: Int,
    val ability: Component,
    val isAbilityHidden: Boolean,
    val eggMove: Component
) : Encodable {
    companion object {
        val NOT_FOUND = FoundPokemon(false, -1, setOf(), 0, 0, Component.empty(), false, Component.empty())

        fun decode(buffer: RegistryFriendlyByteBuf) = FoundPokemon(
            buffer.readBoolean(),
            buffer.readInt(),
            buffer.readList { it.readString() }.toSet(),
            buffer.readInt(),
            buffer.readInt(),
            buffer.readText(),
            buffer.readBoolean(),
            buffer.readText()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeBoolean(found)
        buffer.writeInt(entityId)
        buffer.writeCollection(aspects) { buf, aspect -> buf.writeString(aspect) }
        buffer.writeInt(level)
        buffer.writeInt(potentialStars)
        buffer.writeText(ability)
        buffer.writeBoolean(isAbilityHidden)
        buffer.writeText(eggMove)
    }
}
