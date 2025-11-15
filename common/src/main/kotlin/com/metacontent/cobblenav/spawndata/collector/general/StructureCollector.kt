package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class StructureCollector : GeneralConditionCollector() {
    override val configName = "structures"

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        condition.structures?.let { neededStructures ->
            val component = Component.translatable("gui.cobblenav.spawn_data.structures")
            val structures = neededStructures.map { either ->
                either.map({ it }, { it.location })
            }.toSet()
            builder.structures = structures
            structures.forEach { component.append(Component.translatable(it.toLanguageKey("structure"))) }
            return if (component.siblings.isEmpty()) null else component
        }
        return null
    }
}