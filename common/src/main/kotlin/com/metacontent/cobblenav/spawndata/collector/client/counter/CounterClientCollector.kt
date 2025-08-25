package com.metacontent.cobblenav.spawndata.collector.client.counter

import com.cobblemon.mod.common.ModAPI
import com.metacontent.cobblenav.spawndata.collector.ClientCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency

abstract class CounterClientCollector(api: ModAPI) : ClientCollector, ConfigureableCollector {
    override var neededInstalledMods: List<ModDependency> = if (api == ModAPI.FABRIC) {
        "cobblemon_counter"
    } else {
        "cobbled_counter"
    }.let { listOf(ModDependency(it, "1.6-fabric-1.5.0")) }
    override var neededUninstalledMods: List<ModDependency> = emptyList()
}