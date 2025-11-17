package com.metacontent.cobblenav.spawndata.collector.client.counter

import com.cobblemon.mod.common.ModAPI
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.counter.CounterMod
import us.timinc.mc.cobblemon.counter.api.ClientCounterManager

class StreakCountCollector(api: ModAPI) : CounterClientCollector(api) {
    override val configName = "streak_counts"

    override fun collect(spawnData: SpawnData, player: LocalPlayer): MutableComponent? {
//        val resourceLocation = spawnData.renderable.species.resourceIdentifier
//        val formName = spawnData.renderable.form.name
//        val captureStreak = ClientCounterManager.clientCounterData.getCounter(CounterMod.CounterTypes.CAPTURE).streak
//        val koStreak = ClientCounterManager.clientCounterData.getCounter(CounterMod.CounterTypes.KO).streak
//        val captureCount = if (captureStreak.wouldBreak(resourceLocation, formName)) {
//            0
//        } else {
//            captureStreak.count
//        }
//        val koCount = if (captureStreak.wouldBreak(resourceLocation, formName)) {
//            0
//        } else {
//            koStreak.count
//        }
//        if (captureCount == 0 && koCount == 0) return null
//        return Component.translatable("gui.cobblenav.spawn_data.counter.streak_counts", captureCount, koCount)
        return null
    }
}