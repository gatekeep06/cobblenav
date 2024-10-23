package com.metacontent.neoforge

import com.cobblemon.mod.common.NetworkManager
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.client.net.data.DataRegistrySyncPacketHandler
import com.cobblemon.mod.neoforge.net.NeoForgePacketInfo
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.networking.CobblenavNetwork
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.HandlerThread

object CobblenavNeoForgeNetworkManager : NetworkManager {
    const val PROTOCOL_VERSION = "1.0.0"

    fun registerMessages(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(Cobblenav.ID).versioned(PROTOCOL_VERSION)
        val netRegistrar = event.registrar(Cobblenav.ID).versioned(PROTOCOL_VERSION).executesOn(HandlerThread.NETWORK)

        val syncPackets = HashSet<ResourceLocation>()
        val asyncPackets = HashSet<ResourceLocation>()

        CobblenavNetwork.s2cPayloads.map { NeoForgePacketInfo(it) }.forEach {
            val handleAsync = it.info.handler is DataRegistrySyncPacketHandler<*, *>
            if (handleAsync) asyncPackets += it.info.id
            else syncPackets += it.info.id

            it.registerToClient(if (handleAsync) netRegistrar else registrar)
        }
        CobblenavNetwork.c2sPayloads.map { NeoForgePacketInfo(it) }.forEach { it.registerToServer(registrar) }
    }

    override fun sendPacketToPlayer(player: ServerPlayer, packet: NetworkPacket<*>) {
        player.connection.send(packet)
    }

    override fun sendToServer(packet: NetworkPacket<*>) {
        Minecraft.getInstance().connection?.send(packet)
    }
}