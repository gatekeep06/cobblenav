package com.metacontent.cobblenav.config

import com.cobblemon.mod.common.util.removeIf
import com.metacontent.cobblenav.spawndata.collector.Collector

class CobblenavConfig : Config<CobblenavConfig>() {
    companion object {
        private val collectors = mutableMapOf<String, Boolean>()

        fun addCollector(collectorName: String, defaultValue: Boolean = true) {
            collectors[collectorName] = defaultValue
        }

        fun containsCollector(collectorName: String): Boolean = collectors.containsKey(collectorName)
    }

    @Transient
    override val fileName = "server-config.json"

    val hideUnknownSpawns = false
    val hideConditionsOfUnknownSpawns = true
    val hideNaturalBlockConditions = true
    val percentageForKnownHerd = 0.5f
    val syncLabelsWithClient = true
    val syncEvYieldWithClient = true
    val searchAreaWidth = 128.0
    val searchAreaHeight = 128.0
    val pokemonFeatureWeights = FeatureWeights.BASE
    val collectableConditions = mutableMapOf<String, Boolean>()

    override fun applyToLoadedConfig(default: CobblenavConfig) {
        collectors.forEach { this.collectableConditions.putIfAbsent(it.key, it.value) }
        this.collectableConditions.removeIf { !collectors.keys.contains(it.key) }
    }

    fun collectorEnabled(collector: Collector<*>): Boolean = collectableConditions.contains(collector.name)
}