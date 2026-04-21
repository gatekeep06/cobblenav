package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.networking.packet.client.CatalogueDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestCatalogueDataPacket
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import com.metacontent.cobblenav.util.spawnCatalogue
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object RequestCatalogueDataHandler : ServerNetworkPacketHandler<RequestCatalogueDataPacket> {
    override fun handle(
        packet: RequestCatalogueDataPacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        server.execute {
            val catalogue = player.spawnCatalogue()
            val ids = packet.spawnIds?.filter(catalogue::contains) ?: catalogue.getIds()
            if (ids.isEmpty()) return@execute

            val spawnData = ids.flatMap { SpawnDataHelper.getSpawnData(it, player) }

            CatalogueDataPacket(spawnData).sendToPlayer(player)
        }
    }
}