package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.literal
import net.minecraft.network.chat.MutableComponent

class SkyLightCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "sky_light"
    }

    override val name = NAME
    override val color = 0x87CEEB

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        return formatValueRange(condition.minSkyLight, condition.maxSkyLight)?.let {
            listOf(literal(it))
        }
    }
}