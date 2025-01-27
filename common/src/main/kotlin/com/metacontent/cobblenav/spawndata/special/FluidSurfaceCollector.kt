package com.metacontent.cobblenav.spawndata.special

import com.cobblemon.mod.common.api.spawning.condition.SurfaceTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.spawndata.ConditionCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class FluidSurfaceCollector : ConditionCollector<SurfaceTypeSpawningCondition<*>> {
    override val conditionClass = SurfaceTypeSpawningCondition::class.java

    override fun collect(
        condition: SurfaceTypeSpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        condition.fluid?.toResourceLocation()?.let {
            return Component.translatable("gui.cobblenav.spawn_data.fluid").append(Component.translatable("tag.fluid.c.${it.path}"))
        }
        return null
    }
}