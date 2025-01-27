package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.spawndata.general.*
import com.metacontent.cobblenav.spawndata.special.DepthSubmergedCollector
import com.metacontent.cobblenav.spawndata.special.DepthSurfaceCollector
import com.metacontent.cobblenav.spawndata.special.FluidSubmergedCollector
import com.metacontent.cobblenav.spawndata.special.FluidSurfaceCollector
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

object ConditionCollectors {
    private val generalCollectors = mutableListOf<GeneralConditionCollector>()
    private val collectors = mutableListOf<ConditionCollector<*>>()

    fun registerGeneral(collector: GeneralConditionCollector) {
        generalCollectors += collector
    }

    fun register(collector: ConditionCollector<*>) {
        collectors += collector
    }

    private fun <T : SpawningCondition<*>> getCollectors(condition: T): List<ConditionCollector<T>> {
        @Suppress("UNCHECKED_CAST")
        return collectors.filter { it.supports(condition) } as List<ConditionCollector<T>>
    }

    fun collect(
        condition: SpawningCondition<*>,
        fittingContexts: List<SpawningContext>,
        player: ServerPlayer
    ): List<MutableComponent> {
        return generalCollectors.mapNotNull { it.collect(condition, fittingContexts, player) } +
                getCollectors(condition).mapNotNull { it.collect(condition, fittingContexts, player) }
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
    }
}