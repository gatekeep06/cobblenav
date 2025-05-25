package com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.item.ZygardeCubeChargeCondition
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class ZygardeCubeChargeCollector : ConditionCollector<SpawningCondition<*>> {
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<String> = listOf("mythsandlegends")
    override var neededUninstalledMods: List<String> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        val appendage = condition.appendages.filterIsInstance<ZygardeCubeChargeCondition>().firstOrNull() ?: return null
        if (appendage.required_cells == 0 && appendage.required_cores == 0) return null
        return Component.translatable(
            "gui.cobblenav.spawn_data.mal.zygarde_cube",
            appendage.required_cells,
            appendage.required_cores
        )
    }
}