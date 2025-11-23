package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.AreaTypeSpawningCondition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.resources.ResourceLocation

class AreaTypeBlockCollector : BlockConditionCollector<AreaTypeSpawningCondition<*>>, ConfigureableCollector {
    override val conditionName = "area_type_block"
    override val configName = "area_type_block"
    override val conditionClass = AreaTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(condition: AreaTypeSpawningCondition<*>): Set<ResourceLocation> {
        return condition.neededNearbyBlocks?.toBlockSet() ?: emptySet()
    }
}