package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.MutableComponent

class RodTypeCollector : ConditionCollector<FishingSpawningCondition>() {
    companion object {
        const val NAME = "rod_type"
    }

    override val name = NAME
    override val color = 0xD2691E
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        condition: FishingSpawningCondition
    ): List<MutableComponent>? {
        return condition.rodType?.let { resourceLocation ->
            PokeRods.getPokeRod(resourceLocation)?.pokeBallId?.let {
                listOf(translate(it, "item"))
            }
        }
    }
}