package com.metacontent.cobblenav.spawndata.collector.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.collector.ConfigurableCollector
import com.metacontent.cobblenav.util.toResourceLocation
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

@ConfigurableCollector(BiomeCollector.NAME)
class BiomeCollector : GeneralConditionCollector() {
    companion object {
        const val NAME = "biomes"
    }

    override val name = NAME
    override val color = 0x2E8B57

    override fun collectValues(
        detail: SpawnDetail,
        condition: SpawningCondition<*>,
        player: ServerPlayer
    ): List<MutableComponent>? {
        val biomes = condition.biomes?.mapNotNull { it.toResourceLocation() }?.toSet() ?: return null
        return biomes.map { translate(it, "biome") }
    }
}