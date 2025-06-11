package com.metacontent.cobblenav.event

import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector

interface CustomCollectorRegistrar {
    fun register(collector: ConditionCollector<*>): CustomCollectorRegistrar

    fun register(collector: BlockConditionCollector<*>): CustomCollectorRegistrar
}