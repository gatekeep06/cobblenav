package com.metacontent.cobblenav.networking.packet.server

import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class FindPokemonPacket(val species: String, val aspects: Set<String>) : CobblenavNetworkPacket<FindPokemonPacket> {
    companion object {
        val ID = cobblenavResource("find_pokemon")
        fun decode(buffer: RegistryFriendlyByteBuf) = FindPokemonPacket(
            buffer.readString(),
            buffer.readList { it.readString() }.toSet()
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(species)
        buffer.writeCollection(aspects) { buf, aspect -> buf.writeString(aspect) }
    }
}