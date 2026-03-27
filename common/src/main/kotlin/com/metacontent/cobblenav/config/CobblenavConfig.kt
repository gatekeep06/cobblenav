package com.metacontent.cobblenav.config

import com.cobblemon.mod.common.util.removeIf
import com.metacontent.cobblenav.spawndata.collector.Collector
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder

class CobblenavConfig : Config<CobblenavConfig>() {
    companion object {
        private val collectorPackages = mutableListOf(
            "com.metacontent.cobblenav.spawndata.collector"
        )

        fun registerCollectorPackage(packagePath: String) {
            collectorPackages.add(packagePath)
        }
    }

    @Transient
    override val fileName = "server-config.json"

    @Transient
    private val reflections = Reflections(
        ConfigurationBuilder()
            .forPackages(*collectorPackages.toTypedArray())
            .setScanners(Scanners.TypesAnnotated)
    )

    val hideUnknownSpawns = false
    val hideConditionsOfUnknownSpawns = true
    val hideNaturalBlockConditions = true
    val percentageForKnownHerd = 0.5f
    val syncLabelsWithClient = true
    val searchAreaWidth = 200.0
    val searchAreaHeight = 200.0
    val pokemonFeatureWeights = FeatureWeights.BASE
    val collectableConditions = mutableMapOf<String, Boolean>()

    override fun applyToLoadedConfig(default: CobblenavConfig) {
        val annotatedClasses = reflections.getTypesAnnotatedWith(ConfigurableCollector::class.java)
        val defaultConditions = annotatedClasses.mapNotNull { clazz ->
            clazz.getAnnotation(ConfigurableCollector::class.java)?.let {
                if (!it.enabled) return@mapNotNull null
                it.name to it.defaultConfigValue
            }
        }.toMap()
        defaultConditions.forEach { this.collectableConditions.putIfAbsent(it.key, it.value) }
        this.collectableConditions.removeIf { !defaultConditions.keys.contains(it.key) }
    }

    fun collectorEnabled(collector: Collector<*>): Boolean = collectableConditions.contains(collector.name)
}