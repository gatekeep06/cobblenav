package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.screen.CatalogueScreen
import com.metacontent.cobblenav.networking.packet.client.CatalogueDataPacket
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.Minecraft

object CatalogueDataHandler : ClientNetworkPacketHandler<CatalogueDataPacket> {
    override fun handle(packet: CatalogueDataPacket, client: Minecraft) {
        val grouped = packet.catalogueData.groupBy(SpawnData::id)
        CobblenavClient.spawnDataCatalogue.cachedSpawnData.putAll(grouped)
        (client.screen as? CatalogueScreen)?.populateCatalogue()
    }
}