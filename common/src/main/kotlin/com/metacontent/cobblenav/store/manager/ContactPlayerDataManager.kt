package com.metacontent.cobblenav.store.manager

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreTypes
import com.metacontent.cobblenav.store.ContactPlayerData
import net.minecraft.server.level.ServerPlayer
import java.util.*

object ContactPlayerDataManager : PlayerDataManager<ContactPlayerData> {
    override fun from(data: GeneralPlayerData): ContactPlayerData {
        var contactData = data.extraData[ContactPlayerData.NAME] as? ContactPlayerData
        if (contactData == null) {
            contactData = ContactPlayerData()
            data.extraData[ContactPlayerData.NAME] = contactData
            Cobblemon.playerDataManager.saveSingle(data, PlayerInstancedDataStoreTypes.GENERAL)
        }
        return contactData
    }

    override fun executeFor(player: ServerPlayer, function: (ContactPlayerData) -> Boolean): Boolean {
        val data = Cobblemon.playerDataManager.getGenericData(player)
        val result = function.invoke(from(data))
        Cobblemon.playerDataManager.saveSingle(data, PlayerInstancedDataStoreTypes.GENERAL)
        return result
    }

    override fun executeFor(playerUuid: UUID, function: (ContactPlayerData) -> Boolean): Boolean {
        val data = Cobblemon.playerDataManager.getGenericData(playerUuid)
        val result = function.invoke(from(data))
        Cobblemon.playerDataManager.saveSingle(data, PlayerInstancedDataStoreTypes.GENERAL)
        return result
    }
}