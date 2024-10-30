package com.metacontent.cobblenav.networking.packet.server

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class RequestLocationScreenInitDataPacket : NetworkPacket<RequestLocationScreenInitDataPacket> {
    companion object {
        val ID = cobblenavResource("request_location_screen_init_data")
        fun decode(buffer: RegistryFriendlyByteBuf) = RequestLocationScreenInitDataPacket()
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {}
}