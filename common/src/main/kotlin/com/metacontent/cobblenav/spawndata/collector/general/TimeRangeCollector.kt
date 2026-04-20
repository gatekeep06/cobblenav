package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.getTimeString
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent

class TimeRangeCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "time_range"
    }

    override val name = NAME
    override val color = 0xFF8C00

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        return condition.timeRange?.ranges?.map { ranges ->
            translate(getTimeString(ranges))
        }
    }
}