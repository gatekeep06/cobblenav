package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.spawning.WorldSlice
import com.cobblemon.mod.common.api.spawning.context.AreaContextResolver
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.context.calculators.AreaSpawningContextCalculator
import com.cobblemon.mod.common.api.spawning.context.calculators.AreaSpawningInput
import com.cobblemon.mod.common.api.spawning.spawner.Spawner
import com.cobblemon.mod.common.util.toVec3d
import net.minecraft.core.BlockPos

class PokenavAreaContextResolver : AreaContextResolver {
    override fun resolve(
        spawner: Spawner,
        contextCalculators: List<AreaSpawningContextCalculator<*>>,
        slice: WorldSlice
    ): List<AreaSpawningContext> {
        var pos = BlockPos.MutableBlockPos(1, 2, 3)
        val input = AreaSpawningInput(spawner, pos, slice)
        val contexts = mutableListOf<AreaSpawningContext>()

        var x = slice.baseX
        var y = slice.baseY
        var z = slice.baseZ

        while (x < slice.baseX + slice.length) {
            while (y < slice.baseY + slice.height) {
                while (z < slice.baseZ + slice.width) {
                    pos.set(x, y, z)
                    val fittedContextCalculator = contextCalculators
                        .firstOrNull { calc -> calc.fits(input) && input.spawner.influences.none { !it.isAllowedPosition(input.world, input.position, calc) } }
                    if (fittedContextCalculator != null) {
                        val context = fittedContextCalculator.calculate(input)
                        if (context != null) {
                            contexts.add(context)
                            pos = BlockPos.MutableBlockPos(1, 2, 3)
                            input.position = pos
                        }
                    }
                    z++
                }
                y++
                z = slice.baseZ
            }
            x++
            y = slice.baseY
            z = slice.baseZ
        }
        return contexts
    }
}