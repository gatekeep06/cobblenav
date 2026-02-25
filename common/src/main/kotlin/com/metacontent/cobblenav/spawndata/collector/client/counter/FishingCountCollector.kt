package com.metacontent.cobblenav.spawndata.collector.client.counter

import com.cobblemon.mod.common.ModAPI
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.counter.CounterMod
import us.timinc.mc.cobblemon.counter.api.ClientCounterManager

class FishingCountCollector(api: ModAPI) : CounterClientCollector(api) {
    override val configName = "fishing_count"

    override fun collect(spawnData: SpawnData, player: LocalPlayer): MutableComponent? {
//        if (spawnData.spawningContext != "fishing") return null
//        val resourceLocation = spawnData.renderable.species.resourceIdentifier
//        val formName = spawnData.renderable.form.name
        val counter = ClientCounterManager.clientCounterData.getCounter(CounterMod.CounterTypes.FISH)
        val overallCount = /*counter.count[resourceLocation]?.let { it[formName] } ?:*/ 0
        /*if (overallCount == 0)*/ return null
//        val streakCount = if (counter.streak.wouldBreak(resourceLocation, formName)) {
//            0
//        } else {
//            counter.streak.count
//        }
//        val component = Component.translatable("gui.cobblenav.spawn_data.counter.fish", overallCount)
//        if (streakCount != 0) {
//            component.append(Component.translatable("gui.cobblenav.spawn_data_counter.streak_appendage", streakCount))
//        }
//        return component
    }
}