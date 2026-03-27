package com.metacontent.cobblenav.spawndata.collector

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigurableCollector(
    val name: String,
    val enabled: Boolean = true,
    val defaultConfigValue: Boolean = true
)

fun Collector<*>.isConfigurable() = this.javaClass.isAnnotationPresent(ConfigurableCollector::class.java)