package com.metacontent.cobblenav.api.event.profile

import com.metacontent.cobblenav.storage.ProfilePlayerData

data class ProfileDataCreated(
    val data: ProfilePlayerData
)