package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class StructureCollector : GeneralConditionCollector() {
    override val conditionName = "structures"
    override val conditionColor = 0x8B4513
    override val configName = "structures"

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