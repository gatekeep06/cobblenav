package com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.pokemon.PokemonCondition
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class PokemonCollector : ConditionCollector<SpawningCondition<*>> {
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<String> = listOf("mythsandlegends")
    override var neededUninstalledMods: List<String> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        val pokemonIds = condition.appendages.filterIsInstance<PokemonCondition>()
            .firstOrNull()?.pokemonShowdownIDs ?: return null
        if (pokemonIds.isEmpty()) return null
        val base = Component.translatable("gui.cobblenav.spawn_data.mal.pokemon")
        pokemonIds.forEach {
            // Hopefully all addon pokemon will continue to have the cobblemon namespace
            base.append(Component.translatable("cobblemon.species.$it.name"))
        }
        return base
    }
}