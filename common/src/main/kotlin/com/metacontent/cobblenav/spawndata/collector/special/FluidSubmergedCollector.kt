package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.MutableComponent

class FluidSubmergedCollector : ConditionCollector<SubmergedTypeSpawningCondition<*>>() {
    companion object {
        const val NAME = "fluid_submerged"
    }

    override val name = NAME
    override val color = 0x20B2AA
    override val conditionClass = SubmergedTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        condition: SubmergedTypeSpawningCondition<*>
    ): List<MutableComponent>? {
        return condition.fluid?.toResourceLocation()?.let {
            listOf(translate("tag.fluid.c.${it.path}"))
        }
    }
}