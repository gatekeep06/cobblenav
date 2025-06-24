package com.metacontent.cobblenav.util

import com.metacontent.cobblenav.Cobblenav
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

object ContactSharingManager {
    private val sharing = hashMapOf<UUID, Boolean>()

    fun setSharing(player: ServerPlayer, share: Boolean) = sharing.put(player.uuid, share)

    fun checkSharing(uuid: UUID): Boolean = sharing[uuid] == true

    fun checkSharing(uuids: List<UUID>): Boolean = uuids.map { sharing[it] }.all { it == true }
}