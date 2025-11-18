package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.resources.ResourceLocation

class FishingBlockCollector : BlockConditionCollector<FishingSpawningCondition>, ConfigureableCollector {
    override val conditionName = "fishing_block"
    override val configName = "fishing_block"
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: FishingSpawningCondition
    ): Set<ResourceLocation> {
        return condition.neededNearbyBlocks?.toBlockSet() ?: emptySet()
    }
}