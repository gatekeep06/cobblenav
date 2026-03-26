package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.literal
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class LightCollector : GeneralConditionCollector() {
    override val conditionName = "light"
    override val conditionColor = 0xFFD700
    override val configName = "light"

    override fun collectValues(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return formatValueRange(condition.minLight, condition.maxLight)?.let {
            listOf(literal(it))
        }
    }
}