package com.metacontent.cobblenav.networking

import com.cobblemon.mod.common.net.PacketRegisterInfo
import com.metacontent.cobblenav.networking.handler.client.FoundPokemonHandler
import com.metacontent.cobblenav.networking.handler.client.LocationScreenInitDataHandler
import com.metacontent.cobblenav.networking.handler.client.SpawnMapHandler
import com.metacontent.cobblenav.networking.handler.server.FindPokemonHandler
import com.metacontent.cobblenav.networking.handler.server.RequestLocationScreenInitDataHandler
import com.metacontent.cobblenav.networking.handler.server.RequestSpawnMapHandler
import com.metacontent.cobblenav.networking.handler.server.SavePreferencesHandler
import com.metacontent.cobblenav.networking.packet.client.FoundPokemonPacket
import com.metacontent.cobblenav.networking.packet.client.LocationScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.client.SpawnMapPacket
import com.metacontent.cobblenav.networking.packet.server.FindPokemonPacket
import com.metacontent.cobblenav.networking.packet.server.RequestLocationScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestSpawnMapPacket
import com.metacontent.cobblenav.networking.packet.server.SavePreferencesPacket

object CobblenavNetwork {
    val s2cPayloads = generateS2CPacketInfoList()
    val c2sPayloads = generateC2SPacketInfoList()

    private fun generateS2CPacketInfoList(): List<PacketRegisterInfo<*>> {
        val list = mutableListOf<PacketRegisterInfo<*>>()

        list.add(PacketRegisterInfo(SpawnMapPacket.ID, SpawnMapPacket::decode, SpawnMapHandler))
        list.add(PacketRegisterInfo(LocationScreenInitDataPacket.ID, LocationScreenInitDataPacket::decode, LocationScreenInitDataHandler))
        list.add(PacketRegisterInfo(FoundPokemonPacket.ID, FoundPokemonPacket::decode, FoundPokemonHandler))

        return list
    }

    private fun generateC2SPacketInfoList(): List<PacketRegisterInfo<*>> {
        val list = mutableListOf<PacketRegisterInfo<*>>()

        list.add(PacketRegisterInfo(RequestSpawnMapPacket.ID, RequestSpawnMapPacket::decode, RequestSpawnMapHandler))
        list.add(PacketRegisterInfo(RequestLocationScreenInitDataPacket.ID, RequestLocationScreenInitDataPacket::decode, RequestLocationScreenInitDataHandler))
        list.add(PacketRegisterInfo(SavePreferencesPacket.ID, SavePreferencesPacket::decode, SavePreferencesHandler))
        list.add(PacketRegisterInfo(FindPokemonPacket.ID, FindPokemonPacket::decode, FindPokemonHandler))

        return list
    }
}