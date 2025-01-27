package com.metacontent.cobblenav.spawndata.block

import com.cobblemon.mod.common.api.spawning.condition.AreaTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.metacontent.cobblenav.spawndata.BlockConditionCollector
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class AreaTypeBlockCollector : BlockConditionCollector<AreaTypeSpawningCondition<*>> {
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