package com.metacontent.cobblenav.spawndata.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.spawndata.ConditionCollector
import com.metacontent.cobblenav.spawndata.ConfigureableCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class FluidSubmergedCollector : ConditionCollector<SubmergedTypeSpawningCondition<*>>, ConfigureableCollector {
    override val configName = "fluid_submerged"
    override val conditionClass = SubmergedTypeSpawningCondition::class.java

    override fun collect(
        condition: SubmergedTypeSpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        condition.fluid?.toResourceLocation()?.let {
            return Component.translatable("gui.cobblenav.spawn_data.fluid").append(Component.translatable("tag.fluid.c.${it.path}"))
        }
        return null
    }
}