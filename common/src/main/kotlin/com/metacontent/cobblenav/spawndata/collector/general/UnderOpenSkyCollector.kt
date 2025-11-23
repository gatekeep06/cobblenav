package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class UnderOpenSkyCollector : GeneralConditionCollector() {
    override val configName = "under_open_sky"

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        condition.canSeeSky?.let {
            return Component.translatable("gui.cobblenav.spawn_data.can_see_sky")
                .append(Component.translatable("gui.cobblenav.$it"))
        }
        return null
    }
}