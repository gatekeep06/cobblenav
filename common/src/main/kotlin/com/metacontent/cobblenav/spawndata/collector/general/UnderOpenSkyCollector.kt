package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

@ConfigurableCollector(UnderOpenSkyCollector.NAME)
class UnderOpenSkyCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "under_open_sky"
    }

    override val name = NAME
    override val color = 0x1E90FF

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