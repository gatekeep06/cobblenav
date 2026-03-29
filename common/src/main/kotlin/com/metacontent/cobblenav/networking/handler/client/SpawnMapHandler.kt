package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.LocationScreen
import com.metacontent.cobblenav.networking.packet.client.SpawnMapPacket
import net.minecraft.client.Minecraft

object SpawnMapHandler : ClientNetworkPacketHandler<SpawnMapPacket> {
    override fun handle(packet: SpawnMapPacket, client: Minecraft) {
        val screen = client.screen
        val player = client.player
        if (screen is LocationScreen) {
            if (screen.currentBucket != packet.weightedBucket.name || !screen.loading || player == null) {
                return
            }
//            packet.spawnDataList.forEach {
//                it.conditions.addAll(0, ClientCollectors.collect(it, player))
//            }
            screen.receiveSpawnData(packet.spawnDataList, packet.weightedBucket)
        }
    }
}