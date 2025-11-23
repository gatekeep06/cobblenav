package com.metacontent.cobblenav.spawndata.collector.special.counter

import com.cobblemon.mod.common.ModAPI
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.metacontent.cobblenav.api.platform.SpawnDataContext
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class CountCollector(api: ModAPI) : ConditionCollector<SpawningCondition<*>>, ConfigureableCollector {
    override val configName = "count"
    override val conditionClass = SpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = if (api == ModAPI.FABRIC) {
        "cobblemon_counter"
    } else {
        "cobbled_counter"
    }.let { listOf(ModDependency(it, "1.6-fabric-1.5.0")) }
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(
        condition: SpawningCondition<*>,
        spawnablePositions: List<SpawnablePosition>,
        player: ServerPlayer,
        builder: SpawnDataContext.Builder
    ): MutableComponent? {
//        val appendage = condition.appendages
//            .firstOrNull { it is CountSpawningCondition } as CountSpawningCondition? ?: return null
        val component = Component.empty()
//        appendage.counts?.forEach {
//            component.append(
//                Component.translatable(
//                    "gui.cobblenav.spawn_data.counter.count_condition.${it.type.type}",
//                    it.amount
//                )
//            )
//        }
        if (component.siblings.isEmpty()) return null
        return component
    }
}