package com.metacontent.cobblenav.spawndata

interface ConfigureableCollector {
    val configName: String

    fun present(collectors: List<String>) = collectors.contains(configName)
}