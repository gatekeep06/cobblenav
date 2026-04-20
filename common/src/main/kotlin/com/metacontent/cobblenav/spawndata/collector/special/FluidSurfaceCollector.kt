package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SurfaceTypeSpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.MutableComponent

class FluidSurfaceCollector : ConditionCollector<SurfaceTypeSpawningCondition<*>>() {
    companion object {
        const val NAME = "fluid_surface"
    }

    override val name = NAME
    override val color = 0x5F9EA0
    override val conditionClass = SurfaceTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        condition: SurfaceTypeSpawningCondition<*>
    ): List<MutableComponent>? {
        return condition.fluid?.toResourceLocation()?.let {
            listOf(translate("tag.fluid.c.${it.path}"))
        }
    }
}