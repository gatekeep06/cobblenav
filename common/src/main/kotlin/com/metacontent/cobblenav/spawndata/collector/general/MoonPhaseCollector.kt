package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class MoonPhaseCollector : GeneralConditionCollector() {
    override val conditionName = "moon_phase"
    override val conditionColor = 0x708090
    override val configName = "moon_phase"

    override fun collect(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        return condition.moonPhase?.ranges?.flatMap { range ->
            range.mapNotNull { phase ->
                Component.translatable("moon.cobblenav.${MoonPhase.entries.getOrNull(phase)?.name?.lowercase()}")
            }
        }?.distinct()?.wrap()
    }
}