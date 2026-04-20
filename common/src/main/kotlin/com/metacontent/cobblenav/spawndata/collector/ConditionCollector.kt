package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.MutableComponent

abstract class ConditionCollector<T : SpawningCondition<*>> : Collector<T> {
    fun collect(condition: T): ConditionData? {
        return collectValues(condition)?.let { ConditionData(name, color, it) }
    }

    fun formatValueRange(min: Number?, max: Number?): String? {
        return if (min != null && max != null) {
            "$min - $max"
        } else if (min != null) "≥$min"
        else if (max != null) "≤$max"
        else null
    }

    abstract fun collectValues(condition: T): List<MutableComponent>?
}