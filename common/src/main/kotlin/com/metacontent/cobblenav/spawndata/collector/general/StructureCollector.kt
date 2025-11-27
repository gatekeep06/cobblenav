package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class StructureCollector : GeneralConditionCollector() {
    override val conditionName = "structures"
    override val conditionColor = 0x8B4513
    override val configName = "structures"

    override fun collect(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        return condition.structures?.let { neededStructures ->
            if (neededStructures.isEmpty()) return@let null
            val structures = neededStructures.map { either ->
                either.map({ it }, { it.location })
            }.toSet()
            builder?.structures = structures
            structures.map { Component.translatable(it.toLanguageKey("structure")) }.wrap()
        }
    }
}