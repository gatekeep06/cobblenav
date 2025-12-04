package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.SubmergedTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class DepthSubmergedCollector : ConditionCollector<SubmergedTypeSpawningCondition<*>>, ConfigureableCollector {
    override val conditionName = "depth_submerged"
    override val conditionColor = 0x000080
    override val configName = "depth_submerged"
    override val conditionClass = SubmergedTypeSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        detail: SpawnDetail,
        condition: SubmergedTypeSpawningCondition<*>,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        return formatValueRange(condition.minDepth, condition.maxDepth)?.let {
            listOf(Component.literal(it)).wrap()
        }
    }
}