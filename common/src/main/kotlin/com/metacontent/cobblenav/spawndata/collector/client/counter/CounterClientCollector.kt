package com.metacontent.cobblenav.spawndata.collector.client.counter

import com.cobblemon.mod.common.ModAPI
import com.metacontent.cobblenav.spawndata.collector.ClientCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency

abstract class CounterClientCollector(api: ModAPI) : ClientCollector, ConfigureableCollector {
    override var neededInstalledMods: List<ModDependency> =
        listOf(ModDependency("cobbled_counter", if (api == ModAPI.FABRIC) "1.6-fabric-1.5.2" else "1.6-neoforge-1.5.2"))
    override var neededUninstalledMods: List<ModDependency> = emptyList()
}