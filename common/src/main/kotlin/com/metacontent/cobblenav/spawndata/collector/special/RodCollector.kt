package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class RodCollector : ConditionCollector<FishingSpawningCondition>, ConfigureableCollector {
    override val configName = "rod"
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<String> = emptyList()
    override var neededUninstalledMods: List<String> = emptyList()

    override fun collect(
        condition: FishingSpawningCondition,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        return condition.rod?.toResourceLocation()?.toLanguageKey("item")?.let {
            Component.translatable("gui.cobblenav.spawn_data.rod").append(Component.translatable(it))
        }
    }
}