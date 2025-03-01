package com.metacontent.cobblenav.networking.packet.server

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class RequestFishingMapPacket : CobblenavNetworkPacket<RequestFishingMapPacket> {
    companion object {
        val ID = cobblenavResource("request_fishing_map")
        fun decode(buffer: RegistryFriendlyByteBuf) = RequestFishingMapPacket()
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
    }
}