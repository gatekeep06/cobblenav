package com.metacontent.cobblenav.spawndata.block

import com.cobblemon.mod.common.api.spawning.condition.SeafloorTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.metacontent.cobblenav.spawndata.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.ConfigureableCollector
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class SeafloorTypeBlockCollector : BlockConditionCollector<SeafloorTypeSpawningCondition<*>>, ConfigureableCollector {
    override val configName = "seafloor_type_block"
    override val conditionClass = SeafloorTypeSpawningCondition::class.java

    override fun collect(
        condition: SeafloorTypeSpawningCondition<*>,
        contexts: List<AreaSpawningContext>
    ): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededBaseBlocks?.toBlockList() ?: emptyList()
        contexts.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }
}