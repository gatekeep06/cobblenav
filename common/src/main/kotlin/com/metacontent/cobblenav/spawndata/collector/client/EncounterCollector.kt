package com.metacontent.cobblenav.spawndata.collector.client

import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.spawndata.collector.ClientCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class EncounterCollector : ClientCollector, ConfigureableCollector {
    override val configName = "encounter"
    override var neededInstalledMods: List<String> = emptyList()
    override var neededUninstalledMods: List<String> = emptyList()

    override fun collect(spawnData: SpawnData, player: LocalPlayer): MutableComponent? {
        return Component.translatable("gui.cobblenav.spawn_data.encountered")
            .append(Component.translatable("gui.cobblenav.${spawnData.encountered}"))
    }
}