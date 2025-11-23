package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class CoordinatesCollector : GeneralConditionCollector() {
    override val configName = "coordinates"

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        val coordinates = Component.empty()
        formatValueRange(condition.minX, condition.maxX)?.let {
            coordinates.append(Component.translatable("gui.cobblenav.spawn_data.coordinates.x", "$it "))
        }
        formatValueRange(condition.minZ, condition.maxZ)?.let {
            coordinates.append(Component.translatable("gui.cobblenav.spawn_data.coordinates.z", it))
        }
        return if (coordinates.siblings.isEmpty()) null else coordinates
    }
}