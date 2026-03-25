package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import net.minecraft.resources.ResourceLocation

data class BiomePlatform(
    val id: ResourceLocation,
    val conditions: List<SpawningCondition<*>>,
    val anticonditions: List<SpawningCondition<*>>?
) {
    fun fits(spawnablePosition: SpawnablePosition): Boolean {
        return conditions.any { it.isSatisfiedBy(spawnablePosition) } &&
                anticonditions?.none { it.isSatisfiedBy(spawnablePosition) } != false
    }
}