package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreManager
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import com.metacontent.cobblenav.storage.SpawnDataCatalogue
import net.minecraft.server.level.ServerPlayer
import java.util.*

fun PlayerInstancedDataStoreManager.getSpawnDataCatalogue(playerId: UUID): SpawnDataCatalogue =
    get(playerId, CobblenavDataStoreTypes.SPAWN_DATA) as SpawnDataCatalogue

fun PlayerInstancedDataStoreManager.getSpawnDataCatalogue(player: ServerPlayer): SpawnDataCatalogue =
    getSpawnDataCatalogue(player.uuid)

fun ServerPlayer.spawnCatalogue() = Cobblemon.playerDataManager.getSpawnDataCatalogue(this)