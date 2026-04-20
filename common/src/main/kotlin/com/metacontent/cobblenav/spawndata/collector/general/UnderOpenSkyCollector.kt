package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent

class UnderOpenSkyCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "under_open_sky"
    }

    override val name = NAME
    override val color = 0x1E90FF

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        return condition.canSeeSky?.let {
            return listOf(translate("gui.cobblenav.$it"))
        }
    }
}