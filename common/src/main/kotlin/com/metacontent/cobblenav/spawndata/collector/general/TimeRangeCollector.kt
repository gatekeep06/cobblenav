package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.client.gui.util.getTimeString
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class TimeRangeCollector : GeneralConditionCollector() {
    override val configName = "time_range"

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        condition.timeRange?.let { time ->
            val range = time.ranges.firstOrNull { it.contains(player.level().dayTime % 23999) }
            if (range != null) {
                return Component.translatable("gui.cobblenav.spawn_data.time", getTimeString(range))
            }
        }
        return null
    }
}