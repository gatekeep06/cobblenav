package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import net.minecraft.resources.ResourceLocation

data class BiomePlatform(
    val id: ResourceLocation,
    val condition: SpawningCondition<*>,
    val anticondition: SpawningCondition<*>?
) {
    fun fits(spawnablePosition: SpawnablePosition): Boolean {
        return condition.isSatisfiedBy(spawnablePosition) &&
                anticondition?.isSatisfiedBy(spawnablePosition) != true
    }
}