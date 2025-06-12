package com.metacontent.cobblenav.util.finder

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.readText
import com.cobblemon.mod.common.util.writeString
import com.cobblemon.mod.common.util.writeText
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
    val eggMove: Component,
    val rating: Float
) : Encodable {
    companion object {
        val NOT_FOUND = FoundPokemon(false, -1, setOf(), 0, 0, Component.empty(), false, Component.empty(), 0f)

        fun decode(buffer: RegistryFriendlyByteBuf) = FoundPokemon(
            buffer.readBoolean(),
            buffer.readInt(),
            buffer.readList { it.readString() }.toSet(),
            buffer.readInt(),
            buffer.readInt(),
            buffer.readText(),
            buffer.readBoolean(),
            buffer.readText(),
            buffer.readFloat()
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
        buffer.writeFloat(rating)
    }

    class Builder {
        var found: Boolean = NOT_FOUND.found
        var entityId: Int = NOT_FOUND.entityId
        var aspects: Set<String> = NOT_FOUND.aspects
        var level: Int = NOT_FOUND.level
        var potentialStars: Int = NOT_FOUND.potentialStars
        var ability: Component = NOT_FOUND.ability
        var isAbilityHidden: Boolean = NOT_FOUND.isAbilityHidden
        var eggMove: Component = NOT_FOUND.eggMove
        var rating: Float = NOT_FOUND.rating

        fun build() =
            FoundPokemon(found, entityId, aspects, level, potentialStars, ability, isAbilityHidden, eggMove, rating)
    }
}
