package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.MutableComponent

class LureLevelCollector : ConditionCollector<FishingSpawningCondition>() {
    companion object {
        const val NAME = "lure_level"
    }

    override val name = NAME
    override val color = 0x8A2BE2
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        condition: FishingSpawningCondition
    ): List<MutableComponent>? {
        return formatValueRange(condition.minLureLevel, condition.maxLureLevel)?.let {
            listOf(literal(it))
        }
    }
}