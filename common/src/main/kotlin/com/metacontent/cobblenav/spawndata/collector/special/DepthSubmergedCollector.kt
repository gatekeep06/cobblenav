package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class DepthSubmergedCollector : ConditionCollector<SubmergedTypeSpawningCondition<*>>, ConfigureableCollector {
    override val configName = "depth_submerged"
    override val conditionClass = SubmergedTypeSpawningCondition::class.java

    override fun collect(
        condition: SubmergedTypeSpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        formatValueRange(condition.minDepth, condition.maxDepth)?.let {
            return Component.translatable("gui.cobblenav.spawn_data.depth", it)
        }
        return null
    }
}