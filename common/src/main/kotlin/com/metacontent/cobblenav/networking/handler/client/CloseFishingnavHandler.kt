package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.FishingnavScreen
import com.metacontent.cobblenav.networking.packet.client.CloseFishingnavPacket
import net.minecraft.client.Minecraft

object CloseFishingnavHandler : ClientNetworkPacketHandler<CloseFishingnavPacket> {
    override fun handle(packet: CloseFishingnavPacket, client: Minecraft) {
        (client.screen as? FishingnavScreen)?.onClose()
    }
}