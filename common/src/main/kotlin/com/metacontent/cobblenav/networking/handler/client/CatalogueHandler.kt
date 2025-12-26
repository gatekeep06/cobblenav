package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.CatalogueScreen
import com.metacontent.cobblenav.networking.packet.client.CataloguePacket
import net.minecraft.client.Minecraft

object CatalogueHandler : ClientNetworkPacketHandler<CataloguePacket> {
    override fun handle(packet: CataloguePacket, client: Minecraft) {
        (client.screen as? CatalogueScreen)?.receiveCatalogue(packet.spawnDataList)
    }
}