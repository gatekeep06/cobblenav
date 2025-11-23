package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class WeatherCollector : GeneralConditionCollector() {
    override val configName = "weather"

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        val weather = Component.translatable("gui.cobblenav.spawn_data.weather")
        if (condition.isThundering == true) weather.append(Component.translatable("weather.cobblenav.thunder"))
        if (condition.isRaining == true) weather.append(Component.translatable("weather.cobblenav.rain"))
        if (condition.isRaining == false) weather.append(Component.translatable("weather.cobblenav.clear"))
        return if (weather.siblings.isEmpty()) null else weather
    }
}