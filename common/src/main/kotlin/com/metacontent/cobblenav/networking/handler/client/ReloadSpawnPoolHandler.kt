package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.networking.packet.client.ReloadSpawnPoolPacket
import net.minecraft.client.Minecraft

object ReloadSpawnPoolHandler : ClientNetworkPacketHandler<ReloadSpawnPoolPacket> {
    override fun handle(packet: ReloadSpawnPoolPacket, client: Minecraft) {
        CobblenavClient.spawnDataCatalogue.cachedSpawnData.clear()
    }
}