package com.metacontent.cobblenav.spawndata.collector.special

import com.cobblemon.mod.common.api.fishing.SpawnBaitEffects
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConditionCollector
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class BaitCollector : ConditionCollector<FishingSpawningCondition>() {
    companion object {
        const val NAME = "bait"
    }

    override val name = NAME
    override val color = 0xCD5C5C
    override val conditionClass = FishingSpawningCondition::class.java
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collectValues(
        detail: SpawnDetail,
        condition: FishingSpawningCondition,
        player: ServerPlayer
    ): List<MutableComponent>? {
        return condition.bait?.let { resourceLocation ->
            SpawnBaitEffects.getFromIdentifier(resourceLocation)?.item?.toResourceLocation()?.let {
                listOf(translate(it, "item"))
            }
        }
    }
}