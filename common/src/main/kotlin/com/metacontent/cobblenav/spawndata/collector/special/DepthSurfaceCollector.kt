package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SurfaceTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class DepthSurfaceCollector : ConditionCollector<SurfaceTypeSpawningCondition<*>>() {
    companion object {
        const val NAME = "depth_surface"
    }

    override val name = NAME
    override val color = 0x1E90FF
    override val conditionClass = SurfaceTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        detail: SpawnDetail,
        condition: SurfaceTypeSpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return formatValueRange(condition.minDepth, condition.maxDepth)?.let {
            listOf(literal(it))
        }
    }
}