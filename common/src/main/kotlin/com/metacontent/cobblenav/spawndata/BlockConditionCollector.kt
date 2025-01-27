package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.registry.BlockIdentifierCondition
import com.cobblemon.mod.common.registry.BlockTagCondition
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block

interface BlockConditionCollector<T : SpawningCondition<*>> : SpawningConditionSupporter<T> {
    fun collect(condition: T, contexts: List<AreaSpawningContext>): Set<ResourceLocation>

    fun MutableList<RegistryLikeCondition<Block>>.toBlockList(): List<ResourceLocation> {
        return this.flatMap {
            if (it is BlockIdentifierCondition) {
                return@flatMap listOf(it.identifier)
            }
            if (it is BlockTagCondition) {
                if (it.tag.location.path == "natural") return@flatMap emptyList()
                val optional = BuiltInRegistries.BLOCK.getTag(it.tag)
                if (optional.isPresent) {
                    return@flatMap optional.get().map { blockHolder -> BuiltInRegistries.BLOCK.getKey(blockHolder.value()) }
                }
            }
            return@flatMap emptyList()
        }
    }
}