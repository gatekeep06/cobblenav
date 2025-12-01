package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.client.gui.util.getTimeString
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class TimeRangeCollector : GeneralConditionCollector() {
    override val conditionName = "time_range"
    override val conditionColor = 0xFF8C00
    override val configName = "time_range"

    override fun collect(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        return condition.timeRange?.ranges?.map { ranges ->
            Component.translatable(getTimeString(ranges))
        }?.wrap()
    }
}