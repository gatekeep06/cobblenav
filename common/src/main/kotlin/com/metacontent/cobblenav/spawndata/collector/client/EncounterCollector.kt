package com.metacontent.cobblenav.spawndata.collector.client

import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.spawndata.collector.ClientCollector
import com.metacontent.cobblenav.spawndata.collector.ConfigureableCollector
import com.metacontent.cobblenav.util.ModDependency
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class EncounterCollector : ClientCollector, ConfigureableCollector {
    override val configName = "encounter"
    override var neededInstalledMods: List<ModDependency> = emptyList()
    override var neededUninstalledMods: List<ModDependency> = emptyList()

    override fun collect(spawnData: SpawnData, player: LocalPlayer): MutableComponent? {
        return Component.translatable("gui.cobblenav.spawn_data.encountered")
            .append(Component.translatable("gui.cobblenav.${true}"))
    }
}