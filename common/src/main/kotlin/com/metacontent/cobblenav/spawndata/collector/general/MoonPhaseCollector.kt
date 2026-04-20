package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent

class MoonPhaseCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "moon_phase"
    }

    override val name = NAME
    override val color = 0x708090

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        return condition.moonPhase?.ranges?.flatMap { range ->
            range.mapNotNull { phase ->
                translate("moon.cobblenav.${MoonPhase.entries.getOrNull(phase)?.name?.lowercase()}")
            }
        }?.distinct()
    }
}