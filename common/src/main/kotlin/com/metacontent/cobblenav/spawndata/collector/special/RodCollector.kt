package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.MutableComponent

class RodCollector : ConditionCollector<FishingSpawningCondition>() {
    companion object {
        const val NAME = "rod"
    }

    override val name = NAME
    override val color = 0xA0522D
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        condition: FishingSpawningCondition
    ): List<MutableComponent>? {
        return condition.rod?.toResourceLocation()?.let {
            listOf(translate(it, "item"))
        }
    }
}