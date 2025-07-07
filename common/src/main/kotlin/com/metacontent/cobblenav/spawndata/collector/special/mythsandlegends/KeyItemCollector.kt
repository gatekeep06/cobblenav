package com.metacontent.cobblenav.spawndata.collector.special.mythsandlegends

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.github.d0ctorleon.mythsandlegends.cobblemon.spawning.condition.item.KeyItemCondition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class KeyItemCollector : ConditionCollector<SpawningCondition<*>>, ConfigureableCollector {
    override val configName = "key_item"
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<String> = listOf("mythsandlegends")
    override var neededUninstalledMods: List<String> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        val itemName = condition.appendages.filterIsInstance<KeyItemCondition>().firstOrNull()?.keyItemId ?: return null
        return Component.translatable("gui.cobblenav.spawn_data.mal.key_item")
            .append(Component.translatable(itemName.toLanguageKey("item")))
    }
}