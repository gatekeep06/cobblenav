package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.LocationScreen
import com.metacontent.cobblenav.networking.packet.client.LocationScreenInitDataPacket
import net.minecraft.client.Minecraft

object LocationScreenInitDataHandler : ClientNetworkPacketHandler<LocationScreenInitDataPacket> {
    override fun handle(packet: LocationScreenInitDataPacket, client: Minecraft) {
        val screen = client.screen
        if (screen is LocationScreen) {
            screen.receiveInitData(packet.buckets, packet.biome)
        }
    }
}