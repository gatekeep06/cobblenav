package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.GroundedTypeSpawningCondition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.resources.ResourceLocation

class GroundedTypeBlockCollector : BlockConditionCollector<GroundedTypeSpawningCondition<*>>, ConfigureableCollector {
    override val conditionName = "grounded_type_block"
    override val configName = "grounded_type_block"
    override val conditionClass = GroundedTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: GroundedTypeSpawningCondition<*>
    ): Set<ResourceLocation> {
        return condition.neededBaseBlocks?.toBlockSet() ?: emptySet()
    }
}