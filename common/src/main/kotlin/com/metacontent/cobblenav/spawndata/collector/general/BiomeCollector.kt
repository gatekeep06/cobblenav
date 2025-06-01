package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.util.cobblemonResource
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import kotlin.jvm.optionals.getOrNull

class BiomeCollector : GeneralConditionCollector() {
    override val configName = "biomes"

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        val biome = condition.biomes
            ?.mapNotNull { it.toResourceLocation() }
            ?.filter {
                contexts.any { context ->
                    val registry = context.biomeRegistry
                    val biomeLocation = registry.getKey(context.biome) ?: return@any false
                    val biomeTags = registry.getHolder(biomeLocation).getOrNull()
                        ?.tags()?.map { it.location }?.toList() ?: return@any false
                    return@any biomeLocation == it || biomeTags.contains(it)
                }
            }
            ?.distinct()
            ?.firstOrNull()
        val biomes = Component.translatable("gui.cobblenav.spawn_data.habitat")
        if (biomes.siblings.isEmpty()) biomes.append((biome ?: cobblemonResource("is_overworld")).toLanguageKey("biome"))
        return biomes
    }
}