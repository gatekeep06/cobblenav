package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.metacontent.cobblenav.util.PreferencesSaver
import com.metacontent.cobblenav.networking.packet.server.SavePreferencesPacket
import com.metacontent.cobblenav.util.savedPreferences
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object SavePreferencesHandler : ServerNetworkPacketHandler<SavePreferencesPacket> {
    override fun handle(packet: SavePreferencesPacket, server: MinecraftServer, player: ServerPlayer) {
        server.execute {
            val tag = player.savedPreferences()
            tag.putInt(PreferencesSaver.BUCKET_INDEX_KEY, packet.bucketIndex)
            tag.putString(PreferencesSaver.SORTING_KEY, packet.sorting.name)
            tag.putBoolean(PreferencesSaver.APPLY_BUCKET_KEY, packet.applyBucketChecked)
        }
    }
}