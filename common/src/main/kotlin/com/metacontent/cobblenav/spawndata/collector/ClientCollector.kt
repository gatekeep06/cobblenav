package com.metacontent.cobblenav.spawndata.collector

import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.ModDependant
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.MutableComponent

interface ClientCollector : ModDependant {
    fun collect(spawnData: SpawnData, player: LocalPlayer): MutableComponent?
}