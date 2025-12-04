package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SurfaceTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class DepthSurfaceCollector : ConditionCollector<SurfaceTypeSpawningCondition<*>>, ConfigureableCollector {
    override val conditionName = "depth_surface"
    override val conditionColor = 0x1E90FF
    override val configName = "depth_surface"
    override val conditionClass = SurfaceTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        detail: SpawnDetail,
        condition: SurfaceTypeSpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        return formatValueRange(condition.minDepth, condition.maxDepth)?.let {
            listOf(Component.literal(it)).wrap()
        }
    }
}