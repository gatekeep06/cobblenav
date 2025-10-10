package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.Cobblemon
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.event.CustomClientCollectorRegistrar
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.spawndata.collector.client.EncounterCollector
import com.metacontent.cobblenav.spawndata.collector.client.counter.FishingCountCollector
import com.metacontent.cobblenav.spawndata.collector.client.counter.OverallCountCollector
import com.metacontent.cobblenav.spawndata.collector.client.counter.StreakCountCollector
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.MutableComponent

object ClientCollectors {
    private val collectors = mutableListOf<ClientCollector>()

    internal fun register(collector: ClientCollector) {
        if (collector is ConfigureableCollector && !collector.allowed(CobblenavClient.config.collectableClientConditions)) return
        if (!collector.isModDependencySatisfied()) return
        collectors += collector
    }

    fun collect(spawnData: SpawnData, player: LocalPlayer): List<MutableComponent> {
        return collectors.mapNotNull { it.collect(spawnData, player) }
    }

    fun init() {
        collectors.clear()

        register(EncounterCollector())

        val api = Cobblemon.implementation.modAPI
        register(OverallCountCollector(api))
        register(StreakCountCollector(api))
        register(FishingCountCollector(api))

        CobblenavEvents.REGISTER_CUSTOM_CLIENT_COLLECTORS.emit(object : CustomClientCollectorRegistrar {
            override fun register(collector: ClientCollector): CustomClientCollectorRegistrar {
                ClientCollectors.register(collector)
                return this
            }
        })

        Cobblenav.LOGGER.info("Registered {} client collectors", collectors.size)
    }
}