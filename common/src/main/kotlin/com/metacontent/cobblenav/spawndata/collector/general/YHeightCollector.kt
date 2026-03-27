package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.literal
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class YHeightCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "y_height"
    }

    override val name = NAME
    override val color = 0x4B0082

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