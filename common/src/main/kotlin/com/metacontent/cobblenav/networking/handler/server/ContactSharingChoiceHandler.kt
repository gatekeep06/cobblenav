package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.util.ContactSharingManager
import com.metacontent.cobblenav.networking.packet.server.ContactSharingChoicePacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object ContactSharingChoiceHandler : ServerNetworkPacketHandler<ContactSharingChoicePacket> {
    override fun handle(packet: ContactSharingChoicePacket, server: MinecraftServer, player: ServerPlayer) {
        ContactSharingManager.setSharing(player, packet.choice)
    }
}