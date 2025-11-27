package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.fishing.SpawnBaitEffects
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.api.platform.BiomePlatformContext
import com.metacontent.cobblenav.spawndata.ConditionData
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class BaitCollector : ConditionCollector<FishingSpawningCondition>, ConfigureableCollector {
    override val conditionName = "bait"
    override val conditionColor = 0xCD5C5C
    override val configName = "bait"
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        detail: SpawnDetail,
        condition: FishingSpawningCondition,
        player: ServerPlayer,
        builder: BiomePlatformContext.Builder?
    ): ConditionData? {
        return condition.bait?.let { resourceLocation ->
            SpawnBaitEffects.getFromIdentifier(resourceLocation)?.item?.toResourceLocation()?.toLanguageKey("item")?.let {
                listOf(Component.translatable(it)).wrap()
            }
        }
    }
}