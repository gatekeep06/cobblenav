package com.metacontent.cobblenav.spawndata.collector.client.counter

import com.cobblemon.mod.common.ModAPI
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.counter.CounterModClient
import us.timinc.mc.cobblemon.counter.registry.CounterTypes

class StreakCountCollector(api: ModAPI) : CounterClientCollector(api) {
    override val configName = "streak_counts"

    override fun collect(spawnData: SpawnData, player: LocalPlayer): MutableComponent? {
        val resourceLocation = spawnData.renderable.species.resourceIdentifier
        val formName = spawnData.renderable.form.name
        val captureStreak = CounterModClient.clientCounterData.getCounter(CounterTypes.CAPTURE).streak
        val koStreak = CounterModClient.clientCounterData.getCounter(CounterTypes.KO).streak
        val captureCount = if (captureStreak.wouldBreak(resourceLocation, formName)) {
            0
        } else {
            captureStreak.count
        }
        val koCount = if (captureStreak.wouldBreak(resourceLocation, formName)) {
            0
        } else {
            koStreak.count
        }
        if (captureCount == 0 && koCount == 0) return null
        return Component.translatable("gui.cobblenav.spawn_data.counter.streak_counts", captureCount, koCount)
    }
}