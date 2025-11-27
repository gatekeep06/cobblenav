package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

interface ConditionCollector<T : SpawningCondition<*>> : Collector<T> {
    fun collect(
        detail: SpawnDetail,
        condition: T,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData?

    fun formatValueRange(min: Number?, max: Number?, useSpaces: Boolean = false): String? {
        return if (min != null && max != null) {
            if (useSpaces) "$min - $max" else "$min-$max"
        } else if (min != null) "≥$min"
        else if (max != null) "≤$max"
        else null
    }

    fun List<Component>.wrap() = ConditionData(conditionName, conditionColor, this)
}