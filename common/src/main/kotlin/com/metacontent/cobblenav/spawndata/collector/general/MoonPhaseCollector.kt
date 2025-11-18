package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class MoonPhaseCollector : GeneralConditionCollector() {
    override val configName = "moon_phase"

    override fun collect(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        if (condition.moonPhase == null) return null
        val values = condition.moonPhase!!.ranges.flatMap { ranges ->
            ranges.mapNotNull { phase ->
                Component.translatable("moon.cobblenav.${MoonPhase.entries.getOrNull(phase)?.name?.lowercase()}")
            }
        }
        return ConditionData("moon_phase", values)
    }
}