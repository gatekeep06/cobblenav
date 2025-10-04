package com.metacontent.cobblenav.spawndata.collector.client.counter

import com.metacontent.cobblenav.spawndata.collector.ClientCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency

abstract class CounterClientCollector() : ClientCollector, ConfigureableCollector {
    override var neededInstalledMods: List<ModDependency> = listOf(ModDependency("cobbled_counter", "1.6-fabric-1.5.2"))
    override var neededUninstalledMods: List<ModDependency> = emptyList()
}