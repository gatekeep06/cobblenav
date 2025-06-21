package com.metacontent.cobblenav.spawndata.collector.client.counter

import com.cobblemon.mod.common.ModAPI
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.counter.CounterModClient
import us.timinc.mc.cobblemon.counter.registry.CounterTypes

class OverallCountCollector(api: ModAPI) : CounterClientCollector(api) {
    override val configName = "overall_counts"

    override fun collect(spawnData: SpawnData, player: LocalPlayer): MutableComponent? {
        val resourceLocation = spawnData.renderable.species.resourceIdentifier
        val formName = spawnData.renderable.form.name
        val captureCount = CounterModClient.clientCounterData.getCounter(CounterTypes.CAPTURE)
            .count[resourceLocation]?.let { it[formName] } ?: 0
        val koCount = CounterModClient.clientCounterData.getCounter(CounterTypes.KO)
            .count[resourceLocation]?.let { it[formName] } ?: 0
        if (captureCount == 0 && koCount == 0) return null
        return Component.translatable("gui.cobblenav.spawn_data.counter.overall_counts", captureCount, koCount)
    }
}