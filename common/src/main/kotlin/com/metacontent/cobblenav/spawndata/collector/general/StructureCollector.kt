package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent

class StructureCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "structures"
    }

    override val name = NAME
    override val color = 0x8B4513

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        return condition.structures?.let { neededStructures ->
            if (neededStructures.isEmpty()) return@let null
            val structures = neededStructures.map { either ->
                either.map({ it }, { it.location })
            }.toSet()
            structures.map { translate(it, "structure") }
        }
    }
}