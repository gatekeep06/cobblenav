package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class UnderOpenSkyCollector : GeneralConditionCollector() {
    override val conditionName = "under_open_sky"
    override val conditionColor = 0x1E90FF
    override val configName = "under_open_sky"

    override fun collectValues(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return condition.canSeeSky?.let {
            return listOf(translate("gui.cobblenav.$it"))
        }
    }
}