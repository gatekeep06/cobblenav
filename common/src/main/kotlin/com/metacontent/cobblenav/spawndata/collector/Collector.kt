package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.util.ModDependant

interface Collector<T : SpawningCondition<*>> : ModDependant {
    val conditionClass: Class<T>

    fun supports(condition: SpawningCondition<*>): Boolean {
        return conditionClass.isInstance(condition)
    }
}