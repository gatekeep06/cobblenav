package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

interface ConditionCollector<T : SpawningCondition<*>> : Collector<T> {
    fun collect(
        condition: T,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent?

    fun formatValueRange(min: Number?, max: Number?, useSpaces: Boolean = false): String? {
        return if (min != null && max != null) {
            if (useSpaces) "$min - $max" else "$min-$max"
        } else if (min != null) "≥$min"
        else if (max != null) "≤$max"
        else null
    }
}