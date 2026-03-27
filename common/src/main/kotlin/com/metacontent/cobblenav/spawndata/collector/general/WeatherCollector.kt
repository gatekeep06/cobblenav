package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

@ConfigurableCollector(WeatherCollector.NAME)
class WeatherCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "weather"
    }

    override val name = NAME
    override val color = 0x4682B4

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