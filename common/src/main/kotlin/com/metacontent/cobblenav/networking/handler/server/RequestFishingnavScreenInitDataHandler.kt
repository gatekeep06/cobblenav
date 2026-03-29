package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.metacontent.cobblenav.networking.packet.client.FishingnavScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestFishingnavScreenInitDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

object RequestFishingnavScreenInitDataHandler : ServerNetworkPacketHandler<RequestFishingnavScreenInitDataPacket> {
    override fun handle(packet: RequestFishingnavScreenInitDataPacket, server: MinecraftServer, player: ServerPlayer) {
        server.execute {
            val buckets = Cobblemon.bestSpawner.config.buckets.map { it.name }
            var pokeBall = ResourceLocation.withDefaultNamespace("air")
            var lineColor = ""
            var bait = ItemStack.EMPTY
            (player.fishing as? PokeRodFishingBobberEntity)?.let { bobber ->
                pokeBall = bobber.pokeRodId?.let { PokeRods.getPokeRod(it)?.pokeBallId } ?: pokeBall
                lineColor = bobber.lineColor
                bait = bobber.bobberBait
            }

            FishingnavScreenInitDataPacket(buckets, pokeBall, lineColor, bait).sendToPlayer(player)
        }
    }
}