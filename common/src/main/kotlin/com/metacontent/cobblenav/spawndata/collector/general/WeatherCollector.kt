package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent

class WeatherCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "weather"
    }

    override val name = NAME
    override val color = 0x4682B4

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        val values = mutableListOf<MutableComponent>()
        if (condition.isThundering == true) values.add(translate("weather.cobblenav.thunder"))
        if (condition.isRaining == true) values.add(translate("weather.cobblenav.rain"))
        if (condition.isRaining == false) values.add(translate("weather.cobblenav.clear"))
        return values.ifEmpty { null }
    }
}