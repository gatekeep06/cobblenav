package com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.pokemon.PokemonCondition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class PokemonCollector : ConditionCollector<SpawningCondition<*>>, ConfigureableCollector {
    override val configName = "pokemon"
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = listOf(ModDependency("mythsandlegends", "1.8.0"))
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
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