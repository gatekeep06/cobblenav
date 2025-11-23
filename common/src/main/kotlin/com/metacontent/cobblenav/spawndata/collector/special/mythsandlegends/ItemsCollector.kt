package com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.item.custom.CustomItemsCondition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class ItemsCollector : ConditionCollector<SpawningCondition<*>>, ConfigureableCollector {
    override val configName = "items"
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = listOf(ModDependency("mythsandlegends", "1.8.0"))
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        val items = condition.appendages.filterIsInstance<CustomItemsCondition>()
            .firstOrNull()?.itemConditions ?: return null
        val base = Component.translatable("gui.cobblenav.spawn_data.mal.items")
        items.forEach {
            val itemName = Component.translatable(it.itemId.toLanguageKey("item"))
            base.append(Component.literal("${it.count} x ").append(itemName).append(if (it.isConsume) "*" else ""))
        }
        return base
    }
}