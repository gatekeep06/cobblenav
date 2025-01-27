package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.spawndata.block.AreaTypeBlockCollector
import com.metacontent.cobblenav.spawndata.block.GroundedTypeBlockCollector
import com.metacontent.cobblenav.spawndata.block.SeafloorTypeBlockCollector
import com.metacontent.cobblenav.spawndata.general.*
import com.metacontent.cobblenav.spawndata.special.*
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object ConditionCollectors {
    private val generalCollectors = mutableListOf<GeneralConditionCollector>()
    private val collectors = mutableListOf<ConditionCollector<*>>()
    private val blockCollectors = mutableListOf<BlockConditionCollector<*>>()

    fun registerGeneral(collector: GeneralConditionCollector) {
        generalCollectors += collector
    }

    fun register(collector: ConditionCollector<*>) {
        collectors += collector
    }

    fun registerBlock(collector: BlockConditionCollector<*>) {
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

        registerBlock(AreaTypeBlockCollector())
        registerBlock(GroundedTypeBlockCollector())
        registerBlock(SeafloorTypeBlockCollector())
    }
}