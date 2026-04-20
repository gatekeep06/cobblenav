package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.MutableComponent

class BiomeCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "biomes"
    }

    override val name = NAME
    override val color = 0x2E8B57

    override fun collectValues(
        condition: SpawningCondition<*>
    ): List<MutableComponent>? {
        val biomes = condition.biomes?.mapNotNull { it.toResourceLocation() }?.toSet() ?: return null
        return biomes.map { translate(it, "biome") }
    }
}