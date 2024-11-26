package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.LocationScreen
import com.metacontent.cobblenav.networking.packet.client.SpawnMapPacket
import net.minecraft.client.Minecraft

object SpawnMapHandler : ClientNetworkPacketHandler<SpawnMapPacket> {
    override fun handle(packet: SpawnMapPacket, client: Minecraft) {
        val screen = client.screen
        if (screen is LocationScreen) {
            if (screen.currentBucket.name != packet.bucketName) {
                return
            }
            screen.receiveSpawnData(packet.spawnDataList)
        }
    }
}