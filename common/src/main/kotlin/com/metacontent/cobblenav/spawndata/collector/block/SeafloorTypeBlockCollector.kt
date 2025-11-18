package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.SeafloorTypeSpawningCondition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class SeafloorTypeBlockCollector : BlockConditionCollector<SeafloorTypeSpawningCondition<*>>, ConfigureableCollector {
    override val conditionName = "seafloor_type_block"
    override val configName = "seafloor_type_block"
    override val conditionClass = SeafloorTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SeafloorTypeSpawningCondition<*>
    ): Set<ResourceLocation> {
        return condition.neededBaseBlocks?.toBlockSet() ?: emptySet()
    }
}