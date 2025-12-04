package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class RodTypeCollector : ConditionCollector<FishingSpawningCondition>, ConfigureableCollector {
    override val conditionName = "rod_type"
    override val conditionColor = 0xD2691E
    override val configName = "rod_type"
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        detail: SpawnDetail,
        condition: FishingSpawningCondition,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        return condition.rodType?.let { resourceLocation ->
            PokeRods.getPokeRod(resourceLocation)?.pokeBallId?.toLanguageKey("item")?.let {
                listOf(Component.translatable(it)).wrap()
            }
        }
    }
}