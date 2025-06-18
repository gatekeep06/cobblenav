package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.ModDependant
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition

interface Collector<T : SpawningCondition<*>> : ModDependant {
    val conditionClass: Class<T>

    fun supports(condition: SpawningCondition<*>): Boolean {
        return conditionClass.isInstance(condition)
    }
}