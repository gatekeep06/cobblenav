package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.util.cobblenavResource

object CobblenavDataStoreTypes {
    val PROFILES = PlayerInstancedDataStoreType(
        id = cobblenavResource("profile"),
        decoder = ClientProfilePlayerData::decode,
        afterDecodeAction = ClientProfilePlayerData::afterDecode,
        incrementalAfterDecodeAction = ClientProfilePlayerData::incrementalAfterDecode
    )

    val CONTACTS = PlayerInstancedDataStoreType(
        id = cobblenavResource("contact_data"),
        decoder = ClientContactPlayerData::decode,
        afterDecodeAction = {}
    )

    fun register() {
        PlayerInstancedDataStoreTypes.register(PROFILES)
        PlayerInstancedDataStoreTypes.register(CONTACTS)
    }
}