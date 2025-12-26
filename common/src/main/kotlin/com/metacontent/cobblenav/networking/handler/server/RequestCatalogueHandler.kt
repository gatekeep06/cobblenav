package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.networking.packet.client.CataloguePacket
import com.metacontent.cobblenav.networking.packet.server.RequestCataloguePacket
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import com.metacontent.cobblenav.util.spawnCatalogue
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object RequestCatalogueHandler : ServerNetworkPacketHandler<RequestCataloguePacket> {
    override fun handle(
        packet: RequestCataloguePacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        server.execute {
            val spawnDataList = player.spawnCatalogue().spawnDetailIds.mapNotNull { id ->
                SpawnDataHelper.details[id]?.let { SpawnDataHelper.collect(it, player) }
            }
            CataloguePacket(spawnDataList).sendToPlayer(player)
        }
    }
}