package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.ModDependant
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.MutableComponent

interface ClientCollector : ModDependant {
    fun collect(spawnData: SpawnData, player: LocalPlayer): MutableComponent?
}