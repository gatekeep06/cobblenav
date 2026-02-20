package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.networking.packet.client.FishingMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestFishingMapPacket
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object RequestFishingMapHandler : ServerNetworkPacketHandler<RequestFishingMapPacket> {
    override fun handle(packet: RequestFishingMapPacket, server: MinecraftServer, player: ServerPlayer) {
        server.execute {
            FishingMapPacket(SpawnDataHelper.checkFishingSpawns(player)).sendToPlayer(player)
        }
    }
}