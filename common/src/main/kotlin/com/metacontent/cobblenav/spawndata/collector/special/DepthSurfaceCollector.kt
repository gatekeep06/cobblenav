package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SurfaceTypeSpawningCondition
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.MutableComponent

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
        condition: SurfaceTypeSpawningCondition<*>
    ): List<MutableComponent>? {
        return formatValueRange(condition.minDepth, condition.maxDepth)?.let {
            listOf(literal(it))
        }
    }
}