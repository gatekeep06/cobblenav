package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.util.cobblenavResource
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import kotlin.jvm.optionals.getOrNull

class BiomeCollector : GeneralConditionCollector() {
    override val configName = "biomes"

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        val biomes = condition.biomes
            ?.mapNotNull { it.toResourceLocation() }
            ?.filter { resourceLocation ->
                spawnablePositions.any { context ->
                    val registry = context.biomeRegistry
                    val biomeLocation = registry.getKey(context.biome) ?: return@any false
                    val biomeTags = registry.getHolder(biomeLocation).getOrNull()
                        ?.tags()?.map { it.location }?.toList() ?: return@any false
                    return@any biomeLocation == resourceLocation || biomeTags.contains(resourceLocation)
                }
            }?.toSet()
        val habitat = Component.translatable("gui.cobblenav.spawn_data.habitat")
        biomes?.let { set ->
            builder.biomes = set
            set.forEach { habitat.append(Component.translatable(it.toLanguageKey("biome")))  }
        }
        if (habitat.siblings.isEmpty()) habitat.append(cobblenavResource("any").toLanguageKey("biome"))
        return habitat
    }
}