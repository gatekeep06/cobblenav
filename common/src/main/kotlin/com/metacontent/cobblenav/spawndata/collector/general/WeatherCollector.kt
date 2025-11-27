package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class WeatherCollector : GeneralConditionCollector() {
    override val conditionName = "weather"
    override val conditionColor = 0x4682B4
    override val configName = "weather"

    override fun collect(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        val values = mutableListOf<Component>()
        if (condition.isThundering == true) values.add(Component.translatable("weather.cobblenav.thunder"))
        if (condition.isRaining == true) values.add(Component.translatable("weather.cobblenav.rain"))
        if (condition.isRaining == false) values.add(Component.translatable("weather.cobblenav.clear"))
        return if (values.isNotEmpty()) values.wrap() else null
    }
}