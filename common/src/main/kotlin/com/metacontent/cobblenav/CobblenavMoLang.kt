package com.metacontent.cobblenav

import com.bedrockk.molang.runtime.MoParams
import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.molang.MoLangFunctions
import com.metacontent.cobblenav.api.contact.ContactType
import com.metacontent.cobblenav.api.contact.PokenavContact
import com.metacontent.cobblenav.api.contact.npc.NPCProfiles
import com.metacontent.cobblenav.item.Pokenav
import com.metacontent.cobblenav.storage.ContactPlayerData
import com.metacontent.cobblenav.util.getContactData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.function.Function

object CobblenavMoLang {
    private val playerFunctions = listOf<(Player) -> HashMap<String, Function<MoParams, Any>>> { player ->
        val map = hashMapOf<String, Function<MoParams, Any>>()
        map.put("has_pokenav") { params ->
            return@put DoubleValue(player.inventory.contains { it.item is Pokenav })
        }
        if (player is ServerPlayer) {
            map.put("has_npc_contact") { params ->
                val profile = NPCProfiles.get(ResourceLocation.parse(params.getString(0)))
                    ?: return@put DoubleValue.ZERO
                val id = if (profile.commonForAllEntities) profile.id.toString() else params.getString(1)
                val data = Cobblemon.playerDataManager.getContactData(player)
                return@put DoubleValue(data.find(id) != null)
            }
            map.put("update_npc_contact") { params ->
                val profile = NPCProfiles.get(ResourceLocation.parse(params.getString(0)))
                    ?: return@put DoubleValue.ZERO
                val id = if (profile.commonForAllEntities) profile.id.toString() else params.getString(1)
                val contact = PokenavContact(
                    id = id,
                    type = ContactType.NPC,
                    name = profile.name ?: params.getString(2),
                    battles = hashMapOf()
                )
                val result = ContactPlayerData.executeAndSave(player) { data ->
                    data.updateContacts(contact)
                }
                return@put if (result) DoubleValue.ONE else DoubleValue.ZERO
            }
        }
        map
    }

    fun init() {
        MoLangFunctions.playerFunctions.addAll(playerFunctions)
    }
}