package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.text.bold
import com.metacontent.cobblenav.networking.packet.client.FishingMapPacket
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object FishingMapHandler : ClientNetworkPacketHandler<FishingMapPacket> {
    override fun handle(packet: FishingMapPacket, client: Minecraft) {
        packet.fishingMap.forEach { (t, u) ->
            client.player?.sendSystemMessage(Component.literal(t).bold())
            client.player?.sendSystemMessage(Component.literal(u.joinToString { it.renderable.species.name + ": " + it.spawnChance }))
        }
    }
}