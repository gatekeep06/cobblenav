package com.metacontent.cobblenav.spawndata.collector.block

import com.cobblemon.mod.common.api.spawning.condition.SeafloorTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.position.AreaSpawnablePosition
import com.metacontent.cobblenav.spawndata.collector.BlockConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class SeafloorTypeBlockCollector : BlockConditionCollector<SeafloorTypeSpawningCondition<*>>, ConfigureableCollector {
    override val configName = "seafloor_type_block"
    override val conditionClass = SeafloorTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SeafloorTypeSpawningCondition<*>,
        spawnablePositions: List<AreaSpawnablePosition>
    ): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededBaseBlocks?.toBlockList() ?: emptyList()
        spawnablePositions.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }
}