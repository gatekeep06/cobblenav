package com.metacontent.cobblenav.store

import com.cobblemon.mod.common.api.storage.player.PlayerDataExtension
import com.google.gson.JsonObject

class ContactPlayerData : PlayerDataExtension {
    companion object {
        const val NAME: String = "pokenavContactData"
    }

    var title = ""

    override fun deserialize(json: JsonObject): PlayerDataExtension {
        TODO("Not yet implemented")
    }

    override fun name() = NAME

    override fun serialize(): JsonObject {
        TODO("Not yet implemented")
    }
}