package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.config.CobblenavConfig
import com.metacontent.cobblenav.spawndata.collector.block.*
import com.metacontent.cobblenav.spawndata.collector.general.*
import com.metacontent.cobblenav.spawndata.collector.special.*
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import com.cobblemon.mod.common.platform.events.PlatformEvents

/**
 * Registry of all [ConditionCollector]s and [BlockConditionCollector]s for [SpawnData].
 * Each [ConditionCollector] corresponds to a separate line in the tooltip.
 *
 * [ConfigureableCollector] is an optional interface for collectors. If a collector implements the interface,
 * it can only be registered if the [ConfigureableCollector.configName] value is present in the [CobblenavConfig.collectableConditions] list.
 * Registration of additional [ConfigureableCollector]s should be done when the Cobblenav mod is definitely initialized,
 * for which you can use an event such as, for example, [PlatformEvents.SERVER_STARTING].
 */
object ConditionCollectors {
    /**
     * The [generalCollectors] list contains collectors that cover all basic conditions from [SpawningCondition].
     */
    private val generalCollectors = mutableListOf<GeneralConditionCollector>()
    private val collectors = mutableListOf<ConditionCollector<*>>()
    private val blockCollectors = mutableListOf<BlockConditionCollector<*>>()

    fun registerGeneral(collector: GeneralConditionCollector) {
        if (!collector.allowed(Cobblenav.config.collectableConditions)) return
        if (!collector.isModDependencySatisfied()) return
        generalCollectors += collector
        Cobblenav.LOGGER.info("Registered general collector: ${collector::class.java.simpleName}")
    }

    fun register(collector: ConditionCollector<*>) {
        if (collector is ConfigureableCollector && !collector.allowed(Cobblenav.config.collectableConditions)) return
        if (!collector.isModDependencySatisfied()) return
        collectors += collector
        Cobblenav.LOGGER.info("Registered collector: ${collector::class.java.simpleName}")
    }

    fun registerBlock(collector: BlockConditionCollector<*>) {
        if (collector is ConfigureableCollector && !collector.allowed(Cobblenav.config.collectableConditions)) return
        if (!collector.isModDependencySatisfied()) return
        blockCollectors += collector
        Cobblenav.LOGGER.info("Registered block collector: ${collector::class.java.simpleName}")
    }

    private fun <T : SpawningCondition<*>> getCollectors(condition: T): List<ConditionCollector<T>> {
        return collectors.filter { it.supports(condition) }.filterIsInstance<ConditionCollector<T>>()
    }

    private fun <T : SpawningCondition<*>> getBlockCollectors(condition: T): List<BlockConditionCollector<T>> {
        return blockCollectors.filter { it.supports(condition) }.filterIsInstance<BlockConditionCollector<T>>()
    }

    fun collectConditions(
        condition: SpawningCondition<*>,
        fittingContexts: List<SpawningContext>,
        player: ServerPlayer
    ): List<MutableComponent> {
        return generalCollectors.mapNotNull { it.collect(condition, fittingContexts, player) } +
                getCollectors(condition).mapNotNull { it.collect(condition, fittingContexts, player) }
    }

    fun collectBlockConditions(
        condition: SpawningCondition<*>,
        contexts: List<AreaSpawningContext>
    ): Set<ResourceLocation> {
        return getBlockCollectors(condition).flatMap { it.collect(condition, contexts) }.toSet()
    }

    fun init() {
        registerGeneral(BiomeCollector())
        registerGeneral(MoonPhaseCollector())
        registerGeneral(UnderOpenSkyCollector())
        registerGeneral(YHeightCollector())
        registerGeneral(CoordinatesCollector())
        registerGeneral(LightCollector())
        registerGeneral(SkyLightCollector())
        registerGeneral(WeatherCollector())
        registerGeneral(TimeRangeCollector())
        registerGeneral(StructureCollector())
        registerGeneral(SlimeChunkCollector())

        register(FluidSurfaceCollector())
        register(DepthSurfaceCollector())
        register(FluidSubmergedCollector())
        register(DepthSubmergedCollector())
        register(BaitCollector())
        register(LureLevelCollector())
        register(RodCollector())
        register(RodTypeCollector())

        registerBlock(AreaTypeBlockCollector())
        registerBlock(GroundedTypeBlockCollector())
        registerBlock(SeafloorTypeBlockCollector())
    }
}