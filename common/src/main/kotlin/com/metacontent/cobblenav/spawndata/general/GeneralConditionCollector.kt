package com.metacontent.cobblenav.spawndata.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.spawndata.ConditionCollector

abstract class GeneralConditionCollector : ConditionCollector<SpawningCondition<*>> {
    override val conditionClass = SpawningCondition::class.java
}