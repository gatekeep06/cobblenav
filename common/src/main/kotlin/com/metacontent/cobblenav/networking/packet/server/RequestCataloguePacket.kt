package com.metacontent.cobblenav.networking.packet.server

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class RequestCataloguePacket : CobblenavNetworkPacket<RequestCataloguePacket> {
    companion object {
        val ID = cobblenavResource("request_catalogue")

        fun decode(buffer: RegistryFriendlyByteBuf) = RequestCataloguePacket()
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {}
}