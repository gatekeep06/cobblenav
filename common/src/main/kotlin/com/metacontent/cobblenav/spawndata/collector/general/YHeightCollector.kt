package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.literal
import net.minecraft.network.chat.MutableComponent

class YHeightCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "y_height"
    }

    override val name = NAME
    override val color = 0x4B0082

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        return formatValueRange(condition.minY, condition.maxY)?.let {
            listOf(literal(it))
        }
    }
}