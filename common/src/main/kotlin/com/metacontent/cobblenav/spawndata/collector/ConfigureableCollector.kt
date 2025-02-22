package com.metacontent.cobblenav.spawndata.collector

interface ConfigureableCollector {
    val configName: String

    fun allowed(collectors: List<String>) = collectors.contains(configName)
}