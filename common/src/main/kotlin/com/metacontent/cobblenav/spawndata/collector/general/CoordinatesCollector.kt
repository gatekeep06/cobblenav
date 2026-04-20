package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class CoordinatesCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "coordinates"
    }

    override val name = NAME
    override val color = 0x00008B

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        val values = mutableListOf<MutableComponent>()
        formatValueRange(condition.minX, condition.maxX)?.let { values.add(Component.translatable("gui.cobblenav.spawn_data.coordinates.x", it)) }
        formatValueRange(condition.minZ, condition.maxZ)?.let { values.add(Component.translatable("gui.cobblenav.spawn_data.coordinates.z", it)) }
        return values.ifEmpty { null }
    }
}