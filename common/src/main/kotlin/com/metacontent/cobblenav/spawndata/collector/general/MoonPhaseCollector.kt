package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class MoonPhaseCollector : GeneralConditionCollector() {
    override val conditionName = "moon_phase"
    override val conditionColor = 0x708090
    override val configName = "moon_phase"

    override fun collectValues(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return condition.moonPhase?.ranges?.flatMap { range ->
            range.mapNotNull { phase ->
                translate("moon.cobblenav.${MoonPhase.entries.getOrNull(phase)?.name?.lowercase()}")
            }
        }?.distinct()
    }
}