package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.AreaSpawnablePosition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.config.CobblenavConfig
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.event.CustomCollectorRegistrar
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors.generalCollectors
import com.metacontent.cobblenav.spawndata.collector.block.AreaTypeBlockCollector
import com.metacontent.cobblenav.spawndata.collector.block.FishingBlockCollector
import com.metacontent.cobblenav.spawndata.collector.block.GroundedTypeBlockCollector
import com.metacontent.cobblenav.spawndata.collector.block.SeafloorTypeBlockCollector
import com.metacontent.cobblenav.spawndata.collector.general.*
import com.metacontent.cobblenav.spawndata.collector.special.*
import com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends.ItemsCollector
import com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends.KeyItemCollector
import com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends.PokemonCollector
import com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends.ZygardeCubeChargeCollector
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

/**
 * Registry of all [ConditionCollector]s and [BlockConditionCollector]s for [SpawnData].
 * Each [ConditionCollector] corresponds to a separate line in the tooltip.
 *
 * [ConfigureableCollector] is an optional interface for collectors. If a collector implements the interface,
 * it can only be registered if the [ConfigureableCollector.configName] value is present in the [CobblenavConfig.collectableConditions] list.
 * Registration of additional collectors should be done using the [CobblenavEvents.REGISTER_CUSTOM_COLLECTORS] event.
 */
object ConditionCollectors {
    /**
     * The [generalCollectors] list contains collectors that cover all basic conditions from [SpawningCondition].
     */
    private val generalCollectors = mutableListOf<GeneralConditionCollector>()
    private val collectors = mutableListOf<ConditionCollector<*>>()
    private val blockCollectors = mutableListOf<BlockConditionCollector<*>>()

    private fun registerGeneral(collector: GeneralConditionCollector) {
        if (!collector.allowed(Cobblenav.config.collectableConditions)) return
        generalCollectors += collector
    }

    internal fun register(collector: ConditionCollector<*>) {
        if (collector is ConfigureableCollector && !collector.allowed(Cobblenav.config.collectableConditions)) return
        if (!collector.isModDependencySatisfied()) return
        collectors += collector
    }

    internal fun register(collector: BlockConditionCollector<*>) {
        if (collector is ConfigureableCollector && !collector.allowed(Cobblenav.config.collectableConditions)) return
        if (!collector.isModDependencySatisfied()) return
        blockCollectors += collector
    }

    private fun <T : SpawningCondition<*>> getCollectors(condition: T): List<ConditionCollector<T>> {
        return collectors.filter { it.supports(condition) }.filterIsInstance<ConditionCollector<T>>()
    }

    private fun <T : SpawningCondition<*>> getBlockCollectors(condition: T): List<BlockConditionCollector<T>> {
        return blockCollectors.filter { it.supports(condition) }.filterIsInstance<BlockConditionCollector<T>>()
    }

    fun collectConditions(
        condition: SpawningCondition<*>,
        fittingContexts: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): List<MutableComponent> {
        return generalCollectors.mapNotNull { it.collect(condition, fittingContexts, player, builder) } +
                getCollectors(condition).mapNotNull { it.collect(condition, fittingContexts, player, builder) }
    }

    fun collectBlockConditions(
        condition: SpawningCondition<*>,
        contexts: List<AreaSpawnablePosition>
    ): Set<ResourceLocation> {
        return getBlockCollectors(condition).flatMap { it.collect(condition, contexts) }.toSet()
    }

    fun init() {
        generalCollectors.clear()
        collectors.clear()
        blockCollectors.clear()

        // General
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

        // Special
        register(FluidSurfaceCollector())
        register(DepthSurfaceCollector())
        register(FluidSubmergedCollector())
        register(DepthSubmergedCollector())
        register(BaitCollector())
        register(LureLevelCollector())
        register(RodCollector())
        register(RodTypeCollector())

        // Myths and Legends
        register(KeyItemCollector())
        register(ItemsCollector())
        register(PokemonCollector())
        register(ZygardeCubeChargeCollector())

        // Counter
        val api = Cobblemon.implementation.modAPI
//        register(CountCollector(api))
//        register(StreakCollector(api))

        // Block
        register(AreaTypeBlockCollector())
        register(GroundedTypeBlockCollector())
        register(SeafloorTypeBlockCollector())
        register(FishingBlockCollector())

        CobblenavEvents.REGISTER_CUSTOM_COLLECTORS.emit(object : CustomCollectorRegistrar {
            override fun register(collector: ConditionCollector<*>): CustomCollectorRegistrar {
                ConditionCollectors.register(collector)
                return this
            }

            override fun register(collector: BlockConditionCollector<*>): CustomCollectorRegistrar {
                ConditionCollectors.register(collector)
                return this
            }
        })

        Cobblenav.LOGGER.info("Registered {} collectors and {} block collectors", collectors.size, blockCollectors.size)
    }
}