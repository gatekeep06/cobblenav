package com.metacontent.cobblenav.spawndata.collector.special.counter

import com.cobblemon.mod.common.ModAPI
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.counter.spawningconditions.CountSpawningCondition

class StreakCollector(api: ModAPI) : ConditionCollector<SpawningCondition<*>>, ConfigureableCollector {
    override val configName = "streak"
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<String> = if (api == ModAPI.FABRIC) {
        listOf("cobblemon_counter")
    } else {
        listOf("cobbled_counter")
    }
    override var neededUninstalledMods: List<String> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
        val appendage = condition.appendages
            .firstOrNull { it is CountSpawningCondition } as CountSpawningCondition? ?: return null
        val component = Component.empty()
        appendage.streaks?.forEach {
            component.append(
                Component.translatable(
                    "gui.cobblenav.spawn_data.counter.streak_condition.${it.type.type}",
                    it.amount
                )
            )
        }
        if (component.siblings.isEmpty()) return null
        return component
    }
}