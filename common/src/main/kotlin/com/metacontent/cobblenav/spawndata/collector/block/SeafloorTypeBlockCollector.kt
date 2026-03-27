package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.SeafloorTypeSpawningCondition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.resources.ResourceLocation

@ConfigurableCollector(SeafloorTypeBlockCollector.NAME)
class SeafloorTypeBlockCollector : BlockConditionCollector<SeafloorTypeSpawningCondition<*>> {
    companion object {
        const val NAME = "seafloor_type_block"
    }

    override val name = NAME
    override val conditionClass = SeafloorTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SeafloorTypeSpawningCondition<*>
    ): Set<ResourceLocation> {
        return condition.neededBaseBlocks?.toBlockSet() ?: emptySet()
    }
}