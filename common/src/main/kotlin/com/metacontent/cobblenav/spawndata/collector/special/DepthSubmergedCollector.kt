package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.MutableComponent

class DepthSubmergedCollector : ConditionCollector<SubmergedTypeSpawningCondition<*>>() {
    companion object {
        const val NAME = "depth_submerged"
    }

    override val name = NAME
    override val color = 0x000080
    override val conditionClass = SubmergedTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        condition: SubmergedTypeSpawningCondition<*>
    ): List<MutableComponent>? {
        return formatValueRange(condition.minDepth, condition.maxDepth)?.let {
            listOf(literal(it))
        }
    }
}