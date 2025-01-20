package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.client.settings.PokefinderSettings
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class UpdatePokefinderSettingsPacket(val settings: PokefinderSettings) : CobblenavNetworkPacket<UpdatePokefinderSettingsPacket> {
    companion object {
        val ID = cobblenavResource("update_pokefinder_settings")
        fun decode(buffer: RegistryFriendlyByteBuf) = UpdatePokefinderSettingsPacket(PokefinderSettings.BUF_CODEC.decode(buffer))
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        PokefinderSettings.BUF_CODEC.encode(buffer, settings)
    }
}