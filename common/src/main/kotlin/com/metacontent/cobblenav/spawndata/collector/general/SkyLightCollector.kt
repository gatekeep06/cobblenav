package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.literal
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class SkyLightCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "sky_light"
    }

    override val name = NAME
    override val color = 0x87CEEB

    override fun collectValues(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return formatValueRange(condition.minSkyLight, condition.maxSkyLight)?.let {
            listOf(literal(it))
        }
    }
}