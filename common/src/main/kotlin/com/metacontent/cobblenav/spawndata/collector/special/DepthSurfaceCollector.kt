package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SurfaceTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class DepthSurfaceCollector: ConditionCollector<SurfaceTypeSpawningCondition<*>>, ConfigureableCollector {
    override val configName = "depth_surface"
    override val conditionClass = SurfaceTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SurfaceTypeSpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        formatValueRange(condition.minDepth, condition.maxDepth)?.let {
            return Component.translatable("gui.cobblenav.spawn_data.depth", it)
        }
        return null
    }
}