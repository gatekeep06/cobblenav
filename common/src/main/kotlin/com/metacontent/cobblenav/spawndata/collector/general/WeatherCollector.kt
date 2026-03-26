package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class WeatherCollector : GeneralConditionCollector() {
    override val conditionName = "weather"
    override val conditionColor = 0x4682B4
    override val configName = "weather"

    override fun collectValues(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        val values = mutableListOf<MutableComponent>()
        if (condition.isThundering == true) values.add(translate("weather.cobblenav.thunder"))
        if (condition.isRaining == true) values.add(translate("weather.cobblenav.rain"))
        if (condition.isRaining == false) values.add(translate("weather.cobblenav.clear"))
        return values.ifEmpty { null }
    }
}