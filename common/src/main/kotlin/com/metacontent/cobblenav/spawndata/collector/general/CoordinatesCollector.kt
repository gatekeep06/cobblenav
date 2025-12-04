package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class CoordinatesCollector : GeneralConditionCollector() {
    override val conditionName = "coordinates"
    override val conditionColor = 0x00008B
    override val configName = "coordinates"

    override fun collect(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        val values = mutableListOf<Component>()
        formatValueRange(condition.minX, condition.maxX)?.let { values.add(Component.translatable("gui.cobblenav.spawn_data.coordinates.x", it)) }
        formatValueRange(condition.minZ, condition.maxZ)?.let { values.add(Component.translatable("gui.cobblenav.spawn_data.coordinates.z", it)) }
        return if (values.isNotEmpty()) values.wrap() else null
    }
}