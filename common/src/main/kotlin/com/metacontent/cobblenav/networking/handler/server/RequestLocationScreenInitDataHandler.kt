package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.networking.packet.client.LocationScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestLocationScreenInitDataPacket
import com.metacontent.cobblenav.util.WeightedBucket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object RequestLocationScreenInitDataHandler : ServerNetworkPacketHandler<RequestLocationScreenInitDataPacket> {
    override fun handle(
        packet: RequestLocationScreenInitDataPacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        server.execute {
            val weightSum = Cobblemon.bestSpawner.config.buckets.sumOf { it.weight.toDouble() }
            val buckets = Cobblemon.bestSpawner.config.buckets.map {
                WeightedBucket(it.name, (it.weight / weightSum).toFloat())
            }
            val biome = player.level().getBiome(player.onPos).registeredName

            LocationScreenInitDataPacket(buckets, biome).sendToPlayer(player)
        }
    }
}