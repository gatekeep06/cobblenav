package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.ModDependant
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition

interface Collector<T : SpawningCondition<*>> : SpawningConditionSupporter<T>, ModDependant {
}