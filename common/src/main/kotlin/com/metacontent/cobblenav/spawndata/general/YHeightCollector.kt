package com.metacontent.cobblenav.spawndata.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class YHeightCollector : GeneralConditionCollector() {
    override val configName = "y_height"

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        formatValueRange(condition.minY, condition.maxY, true)?.let {
            return Component.translatable("gui.cobblenav.spawn_data.height", it)
        }
        return null
    }
}