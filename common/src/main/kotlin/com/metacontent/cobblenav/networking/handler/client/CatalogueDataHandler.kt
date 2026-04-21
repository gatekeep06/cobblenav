package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.networking.packet.client.CatalogueDataPacket
import net.minecraft.client.Minecraft

object CatalogueDataHandler : ClientNetworkPacketHandler<CatalogueDataPacket> {
    override fun handle(packet: CatalogueDataPacket, client: Minecraft) {
        //TODO: handle data
    }
}