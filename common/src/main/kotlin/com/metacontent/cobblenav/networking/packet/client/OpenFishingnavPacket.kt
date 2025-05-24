package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class OpenFishingnavPacket(val os: PokenavOS) : CobblenavNetworkPacket<OpenFishingnavPacket> {
    companion object {
        val ID = cobblenavResource("open_fishingnav")
        fun decode(buffer: RegistryFriendlyByteBuf) = OpenFishingnavPacket(
            PokenavOS.decode(buffer)
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        os.encode(buffer)
    }
}