package com.metacontent.cobblenav.networking

import com.cobblemon.mod.common.client.net.data.DataRegistrySyncPacketHandler
import com.cobblemon.mod.common.net.PacketRegisterInfo
import com.metacontent.cobblenav.networking.handler.client.*
import com.metacontent.cobblenav.networking.handler.server.*
import com.metacontent.cobblenav.networking.packet.client.*
import com.metacontent.cobblenav.networking.packet.server.*

object CobblenavNetwork {
    val s2cPayloads = generateS2CPacketInfoList()
    val c2sPayloads = generateC2SPacketInfoList()

    private fun generateS2CPacketInfoList(): List<PacketRegisterInfo<*>> {
        val list = mutableListOf<PacketRegisterInfo<*>>()

        list.add(PacketRegisterInfo(SpawnMapPacket.ID, SpawnMapPacket::decode, SpawnMapHandler))
        list.add(PacketRegisterInfo(LocationScreenInitDataPacket.ID, LocationScreenInitDataPacket::decode, LocationScreenInitDataHandler))
        list.add(PacketRegisterInfo(FoundPokemonPacket.ID, FoundPokemonPacket::decode, FoundPokemonHandler))
        list.add(PacketRegisterInfo(OpenPokenavPacket.ID, OpenPokenavPacket::decode, OpenPokenavHandler))
        list.add(PacketRegisterInfo(OpenFishingnavPacket.ID, OpenFishingnavPacket::decode, OpenFishingnavHandler))
        list.add(PacketRegisterInfo(CloseFishingnavPacket.ID, CloseFishingnavPacket::decode, CloseFishingnavHandler))
        list.add(PacketRegisterInfo(FishingMapPacket.ID, FishingMapPacket::decode, FishingMapHandler))
        list.add(PacketRegisterInfo(FishingnavScreenInitDataPacket.ID, FishingnavScreenInitDataPacket::decode, FishingnavScreenInitDataHandler))

        list.add(PacketRegisterInfo(LabelSyncPacket.ID, LabelSyncPacket::decode, DataRegistrySyncPacketHandler()))

        return list
    }

    private fun generateC2SPacketInfoList(): List<PacketRegisterInfo<*>> {
        val list = mutableListOf<PacketRegisterInfo<*>>()

        list.add(PacketRegisterInfo(RequestSpawnMapPacket.ID, RequestSpawnMapPacket::decode, RequestSpawnMapHandler))
        list.add(PacketRegisterInfo(RequestLocationScreenInitDataPacket.ID, RequestLocationScreenInitDataPacket::decode, RequestLocationScreenInitDataHandler))
        list.add(PacketRegisterInfo(SavePreferencesPacket.ID, SavePreferencesPacket::decode, SavePreferencesHandler))
        list.add(PacketRegisterInfo(FindPokemonPacket.ID, FindPokemonPacket::decode, FindPokemonHandler))
        list.add(PacketRegisterInfo(RequestFishingMapPacket.ID, RequestFishingMapPacket::decode, RequestFishingMapHandler))
        list.add(PacketRegisterInfo(RequestFishingnavScreenInitDataPacket.ID, RequestFishingnavScreenInitDataPacket::decode, RequestFishingnavScreenInitDataHandler))

        return list
    }
}