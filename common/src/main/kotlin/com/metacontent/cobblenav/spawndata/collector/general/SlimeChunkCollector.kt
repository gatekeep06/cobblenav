package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class SlimeChunkCollector : GeneralConditionCollector() {
    override val configName = "slime_chunk"

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        condition.isSlimeChunk?.let {
            return Component.translatable("gui.cobblenav.spawn_data.slime_chunk").append(Component.translatable("gui.cobblenav.$it"))
        }
        return null
    }
}