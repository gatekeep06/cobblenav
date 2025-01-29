package com.metacontent.cobblenav.spawndata.collector

interface ConfigureableCollector {
    val configName: String

    fun present(collectors: List<String>) = collectors.contains(configName)
}