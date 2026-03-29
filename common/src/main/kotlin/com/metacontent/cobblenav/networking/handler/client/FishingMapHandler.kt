package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.FishingnavScreen
import com.metacontent.cobblenav.networking.packet.client.FishingMapPacket
import net.minecraft.client.Minecraft

object FishingMapHandler : ClientNetworkPacketHandler<FishingMapPacket> {
    override fun handle(packet: FishingMapPacket, client: Minecraft) {
//        client.player?.let {
//            packet.fishingMap.values.flatten().forEach { data ->
//                data.conditions.addAll(0, ClientCollectors.collect(data, it))
//            }
//        }
        (client.screen as? FishingnavScreen)?.receiveFishingMap(packet.fishingMap)
    }
}