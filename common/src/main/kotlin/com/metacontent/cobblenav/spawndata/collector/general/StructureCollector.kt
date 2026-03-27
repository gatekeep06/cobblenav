package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

@ConfigurableCollector(StructureCollector.NAME)
class StructureCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "structures"
    }

    override val name = NAME
    override val color = 0x8B4513

    override fun collectValues(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer
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