package com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.item.ZygardeCubeChargeCondition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class ZygardeCubeChargeCollector : ConditionCollector<SpawningCondition<*>>, ConfigureableCollector {
    override val configName = "zygarde_cube_charge"
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = listOf(ModDependency("mythsandlegends", "1.8.0"))
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
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