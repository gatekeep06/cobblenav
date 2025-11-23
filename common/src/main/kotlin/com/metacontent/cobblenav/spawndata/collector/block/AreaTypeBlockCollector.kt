package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.AreaTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.position.AreaSpawnablePosition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class AreaTypeBlockCollector : BlockConditionCollector<AreaTypeSpawningCondition<*>>, ConfigureableCollector {
    override val configName = "area_type_block"
    override val conditionClass = AreaTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(condition: AreaTypeSpawningCondition<*>, spawnablePositions: List<AreaSpawnablePosition>): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededNearbyBlocks?.toBlockList() ?: emptyList()
        spawnablePositions.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }
}