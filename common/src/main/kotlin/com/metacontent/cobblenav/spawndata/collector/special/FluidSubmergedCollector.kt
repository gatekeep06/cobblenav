package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class FluidSubmergedCollector : ConditionCollector<SubmergedTypeSpawningCondition<*>>(), ConfigureableCollector {
    override val conditionName = "fluid_submerged"
    override val conditionColor = 0x20B2AA
    override val configName = "fluid_submerged"
    override val conditionClass = SubmergedTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        detail: SpawnDetail,
        condition: SubmergedTypeSpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return condition.fluid?.toResourceLocation()?.let {
            listOf(translate("tag.fluid.c.${it.path}"))
        }
    }
}