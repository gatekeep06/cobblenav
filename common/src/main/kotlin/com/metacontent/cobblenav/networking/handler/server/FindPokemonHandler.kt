package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.metacontent.cobblenav.networking.packet.server.FindPokemonPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.AABB

object FindPokemonHandler : ServerNetworkPacketHandler<FindPokemonPacket> {
    override fun handle(
        packet: FindPokemonPacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        server.execute {
            val entities = player.serverLevel().getEntitiesOfClass(
                PokemonEntity::class.java,
                AABB.ofSize(
                    player.position(),
                    100.0,
                    100.0,
                    100.0
                )
            ) { pokemonEntity -> pokemonEntity.pokemon.isWild() && pokemonEntity.pokemon.species.name == packet.species && pokemonEntity.pokemon.aspects.containsAll(packet.aspects) }

        }
    }
}