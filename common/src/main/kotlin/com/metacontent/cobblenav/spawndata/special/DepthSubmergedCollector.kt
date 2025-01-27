package com.metacontent.cobblenav.spawndata.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.spawndata.ConditionCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class DepthSubmergedCollector : ConditionCollector<SubmergedTypeSpawningCondition<*>> {
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