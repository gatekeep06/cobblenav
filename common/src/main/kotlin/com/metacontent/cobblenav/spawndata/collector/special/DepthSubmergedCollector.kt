package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

@ConfigurableCollector(DepthSubmergedCollector.NAME)
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
        detail: SpawnDetail,
        condition: SubmergedTypeSpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return formatValueRange(condition.minDepth, condition.maxDepth)?.let {
            listOf(literal(it))
        }
    }
}