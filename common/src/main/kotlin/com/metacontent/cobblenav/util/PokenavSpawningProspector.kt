package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.spawning.WorldSlice
import com.cobblemon.mod.common.api.spawning.spawner.SpawningArea
import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos.blockToSectionCoord
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.status.ChunkStatus

object PokenavSpawningProspector {
    fun prospect(area: SpawningArea): WorldSlice? {
        val world = area.world
        var baseY = area.baseY
        var height = area.height
        if (baseY < world.minBuildHeight) {
            val difference = world.minBuildHeight - baseY
            baseY += difference
            height -= difference
            if (height < 1) return null
        }
        if (baseY + height >= world.maxBuildHeight) {
            val difference = baseY + height - 1 - world.maxBuildHeight
            height -= difference
            if (height < 1) return null
        }

        val defaultState = Blocks.STONE.defaultBlockState()
        val defaultBlockData = WorldSlice.BlockData(defaultState, 0, 0)

        val blocks = Array(area.length) { Array(height) { Array(area.width) { defaultBlockData } } }
        val skyLevel = Array(area.length) { Array(area.width) { world.maxBuildHeight } }
        val pos = BlockPos.MutableBlockPos()

        val chunks = mutableMapOf<Pair<Int, Int>, ChunkAccess?>()
        val yRange = (baseY until baseY + height).reversed()
        val lightingProvider = world.lightEngine
        for (x in area.baseX until area.baseX + area.length) {
            for (z in area.baseZ until area.baseZ + area.width) {
                val query = chunks.computeIfAbsent(Pair(blockToSectionCoord(x), blockToSectionCoord(z))) {
                    world.getChunk(it.first, it.second, ChunkStatus.FULL, false)
                } ?: continue

                var canSeeSky = world.canSeeSkyFromBelowWater(pos.set(x, yRange.first, z))
                for (y in yRange) {
                    val skyLight = lightingProvider.getLayerListener(LightLayer.SKY).getLightValue(pos.set(x, y, z))
                    val state = query.getBlockState(pos.set(x, y, z))
                    blocks[x - area.baseX][y - baseY][z - area.baseZ] = WorldSlice.BlockData(
                        state = state,
                        light = world.getMaxLocalRawBrightness(pos),
                        skyLight = skyLight
                    )
                    if (canSeeSky) {
                        skyLevel[x - area.baseX][z - area.baseZ] = y
                    }
                    if (state.fluidState.isEmpty && !state.`is`(CobblemonBlockTags.SEES_SKY)) {
                        canSeeSky = false
                    }
                }
            }
        }

        return WorldSlice(
            cause = area.cause,
            world = world,
            baseX = area.baseX,
            baseY = baseY,
            baseZ = area.baseZ,
            blocks = blocks,
            skyLevel = skyLevel,
            nearbyEntityPositions = emptyList()
        )
    }
}