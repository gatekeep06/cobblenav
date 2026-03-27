package com.metacontent.cobblenav.spawndata.collector

import com.metacontent.cobblenav.config.CobblenavConfig

fun Collector<*>.isConfigurable() = CobblenavConfig.containsCollector(this.name)