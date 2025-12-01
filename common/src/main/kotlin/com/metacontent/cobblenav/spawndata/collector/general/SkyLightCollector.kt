package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class SkyLightCollector : GeneralConditionCollector() {
    override val conditionName = "sky_light"
    override val conditionColor = 0x87CEEB
    override val configName = "sky_light"

    override fun collect(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        return formatValueRange(condition.minSkyLight, condition.maxSkyLight)?.let {
            listOf(Component.literal(it)).wrap()
        }
    }
}