package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.util.removeIf
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
        data.spawnDetailIds.addAll(packet.added.keys)
        data.cachedSpawnData.putAll(packet.added)
    }
}

object RemoveCatalogueEntriesHandler : CatalogueUpdateHandler<RemoveCatalogueEntriesPacket> {
    override fun handle(packet: RemoveCatalogueEntriesPacket, data: ClientSpawnDataCatalogue) {
        data.spawnDetailIds.removeAll(packet.removed)
        data.cachedSpawnData.removeIf { (key, _) -> packet.removed.contains(key) }
    }
}