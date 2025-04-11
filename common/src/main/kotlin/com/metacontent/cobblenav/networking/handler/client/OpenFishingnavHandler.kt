package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.FishingnavScreen
import com.metacontent.cobblenav.networking.packet.client.OpenFishingnavPacket
import net.minecraft.client.Minecraft

object OpenFishingnavHandler : ClientNetworkPacketHandler<OpenFishingnavPacket> {
    override fun handle(packet: OpenFishingnavPacket, client: Minecraft) {
        client.setScreen(FishingnavScreen(packet.os))
    }
}