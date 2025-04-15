package com.metacontent.cobblenav.storage.adapter

import com.cobblemon.mod.common.api.storage.player.adapter.NbtBackedPlayerData
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import com.metacontent.cobblenav.storage.ProfilePlayerData
import com.mojang.serialization.Codec
import java.util.*

class ProfileDataNbtBackend : NbtBackedPlayerData<ProfilePlayerData>(
    subfolder = "cobblenav/profile",
    type = CobblenavDataStoreTypes.PROFILE
) {
    override val codec: Codec<ProfilePlayerData> = ProfilePlayerData.CODEC
    override val defaultData = { uuid: UUID ->
        ProfilePlayerData(
            uuid = uuid,
            titleId = null,
            grantedTitles = mutableSetOf(),
            partnerPokemonUuid = null,
            partnerPokemonCache = null
        )
    }
}