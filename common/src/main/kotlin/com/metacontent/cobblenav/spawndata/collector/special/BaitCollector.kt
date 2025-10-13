package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.fishing.SpawnBaitEffects
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class BaitCollector : ConditionCollector<FishingSpawningCondition>, ConfigureableCollector {
    override val configName = "bait"
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: FishingSpawningCondition,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        return condition.bait?.let { resourceLocation ->
            SpawnBaitEffects.getFromIdentifier(resourceLocation)?.item?.toResourceLocation()?.toLanguageKey("item")?.let {
                Component
                    .translatable("gui.cobblenav.spawn_data.bait")
                    .append(Component.translatable(it))
            }
        }
    }
}