package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.FishingnavScreen
import com.metacontent.cobblenav.networking.packet.client.FishingnavScreenInitDataPacket
import net.minecraft.client.Minecraft

object FishingnavScreenInitDataHandler : ClientNetworkPacketHandler<FishingnavScreenInitDataPacket> {
    override fun handle(packet: FishingnavScreenInitDataPacket, client: Minecraft) {
        (client.screen as? FishingnavScreen)?.receiveInitData(
            packet.buckets,
            packet.pokeBall,
            packet.lineColor,
            packet.baitItem
        )
    }
}