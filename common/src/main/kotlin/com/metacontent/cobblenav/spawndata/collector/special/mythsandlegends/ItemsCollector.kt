package com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.item.custom.CustomItemsCondition
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class ItemsCollector : ConditionCollector<SpawningCondition<*>> {
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<String> = listOf("mythsandlegends")
    override var neededUninstalledMods: List<String> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
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