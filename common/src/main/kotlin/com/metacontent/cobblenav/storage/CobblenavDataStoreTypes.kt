package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes.register
import com.metacontent.cobblenav.storage.client.ClientSpawnDataCatalogue
import com.metacontent.cobblenav.util.cobblenavResource

object CobblenavDataStoreTypes {
    val SPAWN_DATA = register(
        PlayerInstancedDataStoreType(
            id = cobblenavResource("spawn_data_catalogue"),
            decoder = ClientSpawnDataCatalogue::decode,
            afterDecodeAction = ClientSpawnDataCatalogue::afterDecode,
            incrementalAfterDecodeAction = ClientSpawnDataCatalogue::incrementalAfterDecode
        )
    )
}