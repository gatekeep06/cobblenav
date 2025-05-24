package com.metacontent.cobblenav.networking.packet.server

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class RequestFishingnavScreenInitDataPacket : CobblenavNetworkPacket<RequestFishingnavScreenInitDataPacket> {
    companion object {
        val ID = cobblenavResource("request_fishingnav_screen_init_data")
        fun decode(buffer: RegistryFriendlyByteBuf) = RequestFishingnavScreenInitDataPacket()
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {}
}