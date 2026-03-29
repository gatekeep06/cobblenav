package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.feature.SeasonFeatureHandler
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

fun cobblenavResource(name: String, namespace: String = Cobblenav.ID): ResourceLocation {
    return ResourceLocation.fromNamespaceAndPath(namespace, name)
}

fun log(message: String) {
    Cobblenav.LOGGER.info(message)
}

fun RegistryLikeCondition<*>.toResourceLocation(): ResourceLocation? {
    if (this is RegistryLikeIdentifierCondition) {
        return this.identifier
    }
    if (this is RegistryLikeTagCondition) {
        return this.tag.location
    }
    return null
}

fun <T> combinations(vararg lists: Iterable<T>): List<List<T>> {
    return lists.fold(listOf(listOf())) { acc, list ->
        acc.flatMap { combination ->
            list.map { element ->
                combination + element
            }
        }
    }
}

fun PokemonProperties.createAndGetAsRenderable(level: ServerLevel? = null, pos: BlockPos? = null): RenderablePokemon {
    val pokemon = Pokemon()
    this.apply(pokemon)
    if (level != null && pos != null) {
        SeasonFeatureHandler.updateSeason(pokemon, level, pos)
    }
    return pokemon.asRenderablePokemon()
}