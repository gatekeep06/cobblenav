package com.metacontent.cobblenav.storage.adapter

import com.cobblemon.mod.common.api.storage.player.adapter.NbtBackedPlayerData
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import com.metacontent.cobblenav.storage.SpawnDataCatalogue
import com.mojang.serialization.Codec
import java.util.UUID

class SpawnDataCatalogueNbtBackend : NbtBackedPlayerData<SpawnDataCatalogue>(
    subfolder = "cobblenav/spawndata",
    type = CobblenavDataStoreTypes.SPAWN_DATA
) {
    override val codec: Codec<SpawnDataCatalogue> = SpawnDataCatalogue.CODEC
    override val defaultData = { uuid: UUID ->
        SpawnDataCatalogue(uuid, mutableSetOf())
    }
}