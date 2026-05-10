package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.networking.packet.client.AddCatalogueEntriesPacket
import com.metacontent.cobblenav.networking.packet.client.CatalogueUpdatePacket
import com.metacontent.cobblenav.networking.packet.client.RemoveCatalogueEntriesPacket
import com.metacontent.cobblenav.storage.client.ClientSpawnDataCatalogue
import net.minecraft.client.Minecraft

interface CatalogueUpdateHandler<T : CatalogueUpdatePacket<T>> : ClientNetworkPacketHandler<T> {
    override fun handle(packet: T, client: Minecraft) {
        handle(packet, CobblenavClient.spawnDataCatalogue)
    }

    fun handle(packet: T, data: ClientSpawnDataCatalogue)
}

object AddCatalogueEntriesHandler : CatalogueUpdateHandler<AddCatalogueEntriesPacket> {
    override fun handle(packet: AddCatalogueEntriesPacket, data: ClientSpawnDataCatalogue) {
        data.add(packet.added, true)
    }
}

object RemoveCatalogueEntriesHandler : CatalogueUpdateHandler<RemoveCatalogueEntriesPacket> {
    override fun handle(packet: RemoveCatalogueEntriesPacket, data: ClientSpawnDataCatalogue) {
        data.remove(packet.removed)
    }
}