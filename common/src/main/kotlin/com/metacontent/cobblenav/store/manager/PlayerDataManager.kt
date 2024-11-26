package com.metacontent.cobblenav.store.manager

import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

interface PlayerDataManager<T> {
    fun from(data: GeneralPlayerData): T

    fun executeFor(player: ServerPlayer, function: (T) -> Boolean): Boolean

    fun executeFor(playerUuid: UUID, function: (T) -> Boolean): Boolean
}