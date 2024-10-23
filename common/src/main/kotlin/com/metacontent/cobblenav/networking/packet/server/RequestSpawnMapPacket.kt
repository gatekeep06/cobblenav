package com.metacontent.cobblenav.networking.packet.server

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class RequestSpawnMapPacket(val bucket: String) : CobblenavNetworkPacket<RequestSpawnMapPacket> {
    companion object {
        val ID = cobblenavResource("request_spawn_map")
        fun decode(buffer: RegistryFriendlyByteBuf) = RequestSpawnMapPacket(buffer.readString())
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(bucket)
    }
}