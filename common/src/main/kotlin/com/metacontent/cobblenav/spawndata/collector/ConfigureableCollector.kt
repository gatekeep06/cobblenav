package com.metacontent.cobblenav.spawndata.collector

interface ConfigureableCollector {
    val configName: String

    fun allowed(collectors: Map<String, Boolean>) = collectors[configName] ?: false
}