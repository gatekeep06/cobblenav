package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.networking.packet.client.LocationScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestLocationScreenInitDataPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object RequestLocationScreenInitDataHandler : ServerNetworkPacketHandler<RequestLocationScreenInitDataPacket> {
    override fun handle(
        packet: RequestLocationScreenInitDataPacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        server.execute {
            val buckets = Cobblemon.bestSpawner.config.buckets.map { it.name }
            val biome = player.level().getBiome(player.onPos).registeredName

            LocationScreenInitDataPacket(buckets, biome).sendToPlayer(player)
        }
    }
}