package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class FluidSubmergedCollector : ConditionCollector<SubmergedTypeSpawningCondition<*>>, ConfigureableCollector {
    override val configName = "fluid_submerged"
    override val conditionClass = SubmergedTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SubmergedTypeSpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        condition.fluid?.toResourceLocation()?.let {
            builder.fluid = it
            return Component.translatable("gui.cobblenav.spawn_data.fluid").append(Component.translatable("tag.fluid.c.${it.path}"))
        }
        return null
    }
}