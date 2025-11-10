package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.spawning.SpawningZone
import com.cobblemon.mod.common.api.spawning.influence.SpawningZoneInfluence
import com.cobblemon.mod.common.api.spawning.position.AreaSpawnablePosition
import com.cobblemon.mod.common.api.spawning.position.AreaSpawnablePositionResolver
import com.cobblemon.mod.common.api.spawning.position.calculators.AreaSpawnablePositionCalculator
import com.cobblemon.mod.common.api.spawning.spawner.Spawner

class PokenavSpawnablePositionResolver : AreaSpawnablePositionResolver {
    override fun resolve(
        spawner: Spawner,
        spawnablePositionCalculators: List<AreaSpawnablePositionCalculator<*>>,
        zone: SpawningZone
    ): List<AreaSpawnablePosition> {
        val entitylessZone = SpawningZone(
            cause = zone.cause,
            world = zone.world,
            baseX = zone.baseX,
            baseY = zone.baseY,
            baseZ = zone.baseZ,
            blocks = zone.blocks,
            skyLevel = zone.skyLevel,
            nearbyEntityPositions = emptyList(),
            influences = zone.conditionalInfluences as List<SpawningZoneInfluence> + (zone.unconditionalInfluences as? List<SpawningZoneInfluence> ?: emptyList())
        )
        return super.resolve(spawner, spawnablePositionCalculators, entitylessZone)
    }
}