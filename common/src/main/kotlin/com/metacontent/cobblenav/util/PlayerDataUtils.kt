package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreManager
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import com.metacontent.cobblenav.storage.ContactPlayerData
import com.metacontent.cobblenav.storage.ProfilePlayerData
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

fun PlayerInstancedDataStoreManager.getProfileData(playerId: UUID): ProfilePlayerData {
    return get(playerId, CobblenavDataStoreTypes.PROFILES) as ProfilePlayerData
}

fun PlayerInstancedDataStoreManager.getProfileData(player: ServerPlayer): ProfilePlayerData {
    return getProfileData(player.uuid)
}

fun PlayerInstancedDataStoreManager.getContactData(playerId: UUID): ContactPlayerData {
    return get(playerId, CobblenavDataStoreTypes.CONTACTS) as ContactPlayerData
}

fun PlayerInstancedDataStoreManager.getContactData(player: ServerPlayer): ContactPlayerData {
    return getContactData(player.uuid)
}