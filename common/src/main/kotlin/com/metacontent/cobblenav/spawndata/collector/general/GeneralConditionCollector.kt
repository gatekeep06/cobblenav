package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector

abstract class GeneralConditionCollector : ConditionCollector<SpawningCondition<*>>, ConfigureableCollector {
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<String> = emptyList()
    override var neededUninstalledMods: List<String> = emptyList()
}