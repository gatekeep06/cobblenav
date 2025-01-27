package com.metacontent.cobblenav.spawndata.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class SkyLightCollector : GeneralConditionCollector() {
    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        formatValueRange(condition.minSkyLight, condition.maxSkyLight)?.let {
            return Component.translatable("gui.cobblenav.spawn_data.sky_light", it)
        }
        return null
    }
}