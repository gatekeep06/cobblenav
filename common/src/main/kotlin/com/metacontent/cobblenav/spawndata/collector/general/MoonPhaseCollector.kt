package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class MoonPhaseCollector : GeneralConditionCollector() {
    override val configName = "moon_phase"

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        if (condition.moonPhase != null) {
            return Component.translatable("gui.cobblenav.spawn_data.moon")
                .append(Component.translatable("moon.cobblenav.${MoonPhase.ofWorld(player.level()).name.lowercase()}"))
        }
        return null
    }
}