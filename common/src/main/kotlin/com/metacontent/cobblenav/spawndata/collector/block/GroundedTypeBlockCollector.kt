package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.GroundedTypeSpawningCondition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.resources.ResourceLocation

@ConfigurableCollector(GroundedTypeBlockCollector.NAME)
class GroundedTypeBlockCollector : BlockConditionCollector<GroundedTypeSpawningCondition<*>> {
    companion object {
        const val NAME = "grounded_type_block"
    }

    override val name = NAME
    override val conditionClass = GroundedTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: GroundedTypeSpawningCondition<*>
    ): Set<ResourceLocation> {
        return condition.neededBaseBlocks?.toBlockSet() ?: emptySet()
    }
}