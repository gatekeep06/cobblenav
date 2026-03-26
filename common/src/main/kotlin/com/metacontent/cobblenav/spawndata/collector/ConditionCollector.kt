package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

abstract class ConditionCollector<T : SpawningCondition<*>> : Collector<T> {
    fun collect(
        detail: SpawnDetail,
        condition: T,
        player: ServerPlayer
    ): ConditionData? {
        return collectValues(detail, condition, player)?.let { ConditionData(conditionName, conditionColor, it) }
    }

    fun formatValueRange(min: Number?, max: Number?): String? {
        return if (min != null && max != null) {
            "$min - $max"
        } else if (min != null) "≥$min"
        else if (max != null) "≤$max"
        else null
    }

    abstract fun collectValues(detail: SpawnDetail, condition: T, player: ServerPlayer): List<MutableComponent>?
}