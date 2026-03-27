package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

@ConfigurableCollector(LightCollector.NAME)
class LightCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "light"
    }

    override val name = NAME
    override val color = 0xFFD700

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