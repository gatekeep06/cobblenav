package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

class CloseFishingnavPacket : CobblenavNetworkPacket<CloseFishingnavPacket> {
    companion object {
        val ID = cobblenavResource("close_fishingnav")
        fun decode(buffer: RegistryFriendlyByteBuf) = CloseFishingnavPacket()
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
    }
}