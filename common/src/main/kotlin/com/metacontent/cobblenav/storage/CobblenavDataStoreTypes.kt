package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes.register
import com.metacontent.cobblenav.util.cobblenavResource

object CobblenavDataStoreTypes {
    val PROFILES = register(PlayerInstancedDataStoreType(
        id = cobblenavResource("profile"),
        decoder = ClientProfilePlayerData::decode,
        afterDecodeAction = ClientProfilePlayerData::afterDecode,
        incrementalAfterDecodeAction = ClientProfilePlayerData::incrementalAfterDecode
    ))

    val CONTACTS = register(PlayerInstancedDataStoreType(
        id = cobblenavResource("contact_data"),
        decoder = ClientContactPlayerData::decode,
        afterDecodeAction = ClientContactPlayerData::afterDecode,
        incrementalAfterDecodeAction = ClientContactPlayerData::incrementalAfterDecode
    ))
}