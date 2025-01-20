package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.networking.packet.client.FoundPokemonPacket
import com.metacontent.cobblenav.networking.packet.server.FindPokemonPacket
import com.metacontent.cobblenav.util.finder.BestPokemonFinder
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
            val width = Cobblenav.config.searchAreaWidth
            val height = Cobblenav.config.searchAreaHeight
            val pokemonEntities = player.serverLevel().getEntitiesOfClass(
                PokemonEntity::class.java,
                AABB.ofSize(
                    player.position(),
                    width,
                    height,
                    width
                )
            ) {
                pokemonEntity -> pokemonEntity.pokemon.isWild() && pokemonEntity.pokemon.species.name == packet.species && pokemonEntity.pokemon.aspects.containsAll(packet.aspects)
            }
            FoundPokemonPacket(BestPokemonFinder.select(pokemonEntities, player)).sendToPlayer(player)
        }
    }
}