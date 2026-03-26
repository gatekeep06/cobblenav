package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.spawndata.ConditionData
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class LureLevelCollector : ConditionCollector<FishingSpawningCondition>(), ConfigureableCollector {
    override val conditionName = "lure_level"
    override val conditionColor = 0x8A2BE2
    override val configName = "lure_level"
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        detail: SpawnDetail,
        condition: FishingSpawningCondition,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return formatValueRange(condition.minLureLevel, condition.maxLureLevel)?.let {
            listOf(literal(it))
        }
    }
}