package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes.register
import com.metacontent.cobblenav.storage.client.ClientContactPlayerData
import com.metacontent.cobblenav.storage.client.ClientProfilePlayerData
import com.metacontent.cobblenav.util.cobblenavResource

object CobblenavDataStoreTypes {
    val PROFILE = register(PlayerInstancedDataStoreType(
        id = cobblenavResource("profile"),
        decoder = ClientProfilePlayerData::decode,
        afterDecodeAction = ClientProfilePlayerData::afterDecode,
        incrementalAfterDecodeAction = ClientProfilePlayerData::incrementalAfterDecode
    ))

    val CONTACTS = register(PlayerInstancedDataStoreType(
        id = cobblenavResource("contacts"),
        decoder = ClientContactPlayerData::decode,
        afterDecodeAction = ClientContactPlayerData::afterDecode,
        incrementalAfterDecodeAction = ClientContactPlayerData::incrementalAfterDecode
    ))
}