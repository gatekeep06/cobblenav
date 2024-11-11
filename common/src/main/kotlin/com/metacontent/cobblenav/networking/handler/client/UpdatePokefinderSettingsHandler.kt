package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.networking.packet.client.UpdatePokefinderSettingsPacket
import net.minecraft.client.Minecraft

object UpdatePokefinderSettingsHandler : ClientNetworkPacketHandler<UpdatePokefinderSettingsPacket> {
    override fun handle(packet: UpdatePokefinderSettingsPacket, client: Minecraft) {
        CobblenavClient.pokefinderOverlay.settings?.merge(packet.settings)
    }
}