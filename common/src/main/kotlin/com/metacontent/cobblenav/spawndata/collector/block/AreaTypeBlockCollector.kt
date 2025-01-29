package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.AreaTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class AreaTypeBlockCollector : BlockConditionCollector<AreaTypeSpawningCondition<*>>, ConfigureableCollector {
    override val configName = "area_type_block"
    override val conditionClass = AreaTypeSpawningCondition::class.java

    override fun collect(condition: AreaTypeSpawningCondition<*>, contexts: List<AreaSpawningContext>): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededNearbyBlocks?.toBlockList() ?: emptyList()
        contexts.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }
}