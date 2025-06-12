package com.metacontent.cobblenav.spawndata.collector.client.counter

import com.cobblemon.mod.common.ModAPI
import com.metacontent.cobblenav.spawndata.collector.ClientCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector

abstract class CounterClientCollector(api: ModAPI) : ClientCollector, ConfigureableCollector {
    override var neededInstalledMods: List<String> = if (api == ModAPI.FABRIC) {
        listOf("cobblemon_counter")
    } else {
        listOf("cobbled_counter")
    }
    override var neededUninstalledMods: List<String> = emptyList()
}