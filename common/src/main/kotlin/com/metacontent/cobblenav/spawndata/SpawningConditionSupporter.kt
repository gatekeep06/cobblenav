package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition

interface SpawningConditionSupporter<T : SpawningCondition<*>> {
    val conditionClass: Class<T>

    fun supports(condition: SpawningCondition<*>) = conditionClass.isInstance(condition)
}