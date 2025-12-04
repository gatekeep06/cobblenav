package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class BiomeCollector : GeneralConditionCollector() {
    override val conditionName = "biomes"
    override val conditionColor = 0x2E8B57
    override val configName = "biomes"

    override fun collect(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        val biomes = condition.biomes?.mapNotNull { it.toResourceLocation() }?.toSet() ?: return null
        builder?.biomes = biomes
        return biomes.map { Component.translatable(it.toLanguageKey("biome")) }.wrap()
    }
}