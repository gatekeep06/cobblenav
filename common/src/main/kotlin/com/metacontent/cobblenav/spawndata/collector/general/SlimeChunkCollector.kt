package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent

class SlimeChunkCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "slime_chunk"
    }

    override val name = NAME
    override val color = 0x32CD32

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        return condition.isSlimeChunk?.let {
            listOf(translate("gui.cobblenav.$it"))
        }
    }
}