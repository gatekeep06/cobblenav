package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.FishingnavScreen
import com.metacontent.cobblenav.networking.packet.client.CloseFishingnavPacket
import net.minecraft.client.Minecraft

object CloseFishingnavHandler : ClientNetworkPacketHandler<CloseFishingnavPacket> {
    override fun handle(packet: CloseFishingnavPacket, client: Minecraft) {
        (client.screen as? FishingnavScreen)?.let {
            client.player?.playSound(CobblemonSounds.EVOLUTION_NOTIFICATION, 0.6f, 1.5f)
            it.onClose()
        }
    }
}