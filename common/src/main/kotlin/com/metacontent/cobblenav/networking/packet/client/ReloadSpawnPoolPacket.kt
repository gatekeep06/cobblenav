package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class ReloadSpawnPoolPacket : CobblenavNetworkPacket<ReloadSpawnPoolPacket> {
    companion object {
        val ID = cobblenavResource("reload_spawn_pool")
        fun decode(buffer: RegistryFriendlyByteBuf) = ReloadSpawnPoolPacket()
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {}
}