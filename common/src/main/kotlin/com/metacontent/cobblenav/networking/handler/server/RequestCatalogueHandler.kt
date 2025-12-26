package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.networking.packet.server.RequestCataloguePacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object RequestCatalogueHandler : ServerNetworkPacketHandler<RequestCataloguePacket> {
    override fun handle(
        packet: RequestCataloguePacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        server.execute {

        }
    }
}