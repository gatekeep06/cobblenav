package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.AreaTypeSpawningCondition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.resources.ResourceLocation

@ConfigurableCollector(AreaTypeBlockCollector.NAME)
class AreaTypeBlockCollector : BlockConditionCollector<AreaTypeSpawningCondition<*>> {
    companion object {
        const val NAME = "area_type_block"
    }

    override val name = NAME
    override val conditionClass = AreaTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(condition: AreaTypeSpawningCondition<*>): Set<ResourceLocation> {
        return condition.neededNearbyBlocks?.toBlockSet() ?: emptySet()
    }
}