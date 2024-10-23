package com.metacontent.cobblenav.networking.packet

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.server.level.ServerPlayer

interface CobblenavNetworkPacket<T : NetworkPacket<T>> : NetworkPacket<T> {
    override fun sendToServer() {
        Cobblenav.implementation.networkManager.sendToServer(this)
    }

    override fun sendToPlayer(player: ServerPlayer) = Cobblenav.implementation.networkManager.sendPacketToPlayer(player, this)
}