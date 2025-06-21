package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class BaitCollector : ConditionCollector<FishingSpawningCondition>, ConfigureableCollector {
    override val configName = "bait"
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<String> = emptyList()
    override var neededUninstalledMods: List<String> = emptyList()

    override fun collect(
        condition: FishingSpawningCondition,
        contexts: List<SpawningContext>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        return condition.bait?.let { resourceLocation ->
            FishingBaits.getFromIdentifier(resourceLocation)?.let { bait ->
                Component.translatable("gui.cobblenav.spawn_data.bait").append(Component.translatable(bait.item.toLanguageKey("item")))
            }
        }
    }
}