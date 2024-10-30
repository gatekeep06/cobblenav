package com.metacontent.cobblenav.util.finder

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility
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

    data class Builder(
        var found: Boolean = NOT_FOUND.found,
        var entityId: Int = NOT_FOUND.entityId,
        var aspects: Set<String> = NOT_FOUND.aspects,
        var level: Int = NOT_FOUND.level,
        var potentialStars: Int = NOT_FOUND.potentialStars,
        var ability: Component = NOT_FOUND.ability,
        var isAbilityHidden: Boolean = NOT_FOUND.isAbilityHidden,
        var eggMove: Component = NOT_FOUND.eggMove,
        var rating: Float = NOT_FOUND.rating
    ) {
        fun found(found: Boolean) = apply { this.found = found }
        fun entityId(entityId: Int) = apply { this.entityId = entityId }
        fun aspects(aspects: Set<String>) = apply { this.aspects = aspects }
        fun level(level: Int) = apply { this.level = level }
        fun potentialStars(potentialStars: Int) = apply { this.potentialStars = potentialStars }
        fun ability(ability: Component) = apply { this.ability = ability }
        fun abilityHidden(isAbilityHidden: Boolean) = apply { this.isAbilityHidden = isAbilityHidden }
        fun eggMove(eggMove: Component) = apply { this.eggMove = eggMove }
        fun rating(rating: Float) = apply { this.rating = rating }

        fun build() = FoundPokemon(found, entityId, aspects, level, potentialStars, ability, isAbilityHidden, eggMove, rating)
    }
}
