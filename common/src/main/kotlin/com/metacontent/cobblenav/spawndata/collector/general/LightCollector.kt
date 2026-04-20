package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.literal
import net.minecraft.network.chat.MutableComponent

class LightCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "light"
    }

    override val name = NAME
    override val color = 0xFFD700

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        return formatValueRange(condition.minLight, condition.maxLight)?.let {
            listOf(literal(it))
        }
    }
}