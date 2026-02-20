package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.networking.packet.client.SpawnMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestSpawnMapPacket
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object RequestSpawnMapHandler : ServerNetworkPacketHandler<RequestSpawnMapPacket> {
    override fun handle(
        packet: RequestSpawnMapPacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        server.execute {
            val (weightedBucket, spawnDataList) = packet.fixedAreaPoint?.let {
                SpawnDataHelper.checkFixedAreaSpawns(it, player, packet.bucket)
            } ?: SpawnDataHelper.checkPlayerSpawns(player, packet.bucket)
            SpawnMapPacket(weightedBucket, spawnDataList).sendToPlayer(player)
        }
    }
}