package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.screen.LocationScreen
import com.metacontent.cobblenav.networking.packet.client.OpenPokenavPacket
import net.minecraft.client.Minecraft

object OpenPokenavHandler : ClientNetworkPacketHandler<OpenPokenavPacket> {
    override fun handle(packet: OpenPokenavPacket, client: Minecraft) {
        if (CobblenavClient.trackArrowOverlay.tracking) {
            CobblenavClient.trackArrowOverlay.tracking = false
        }
        else {
            client.setScreen(LocationScreen(packet.os, makeOpeningSound = true, animateOpening = true, packet.fixedAreaPoint))
        }
    }
}