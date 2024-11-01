package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.finder.FoundPokemon
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class FoundPokemonPacket(val pokemon: FoundPokemon) : CobblenavNetworkPacket<FoundPokemonPacket> {
    companion object {
        val ID = cobblenavResource("found_pokemon")
        fun decode(buffer: RegistryFriendlyByteBuf) = FoundPokemonPacket(FoundPokemon.decode(buffer))
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        pokemon.encode(buffer)
    }
}