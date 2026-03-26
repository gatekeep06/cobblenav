package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.literal
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class YHeightCollector : GeneralConditionCollector() {
    override val conditionName = "y_height"
    override val conditionColor = 0x4B0082
    override val configName = "y_height"

    override fun collectValues(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return formatValueRange(condition.minY, condition.maxY)?.let {
            listOf(literal(it))
        }
    }
}