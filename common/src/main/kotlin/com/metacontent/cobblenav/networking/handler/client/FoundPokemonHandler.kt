package com.metacontent.cobblenav.networking.handler.client

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.metacontent.cobblenav.client.gui.screen.pokenav.FinderScreen
import com.metacontent.cobblenav.networking.packet.client.FoundPokemonPacket
import net.minecraft.client.Minecraft

object FoundPokemonHandler : ClientNetworkPacketHandler<FoundPokemonPacket> {
    override fun handle(packet: FoundPokemonPacket, client: Minecraft) {
        val screen = client.screen
        if (screen is FinderScreen) {
            screen.receiveFoundPokemon(packet.pokemon)
        }
    }
}