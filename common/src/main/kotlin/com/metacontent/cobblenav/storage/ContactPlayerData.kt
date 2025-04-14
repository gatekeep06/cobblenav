package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer
import com.metacontent.cobblenav.api.contact.ContactID
import com.metacontent.cobblenav.api.contact.PokenavContact
import com.metacontent.cobblenav.storage.client.ClientContactPlayerData
import com.metacontent.cobblenav.util.getContactData
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerPlayer
import java.util.*

data class ContactPlayerData(
    override val uuid: UUID,
    val contacts: MutableMap<ContactID, PokenavContact>
) : InstancedPlayerData {
    companion object {
        val CODEC: Codec<ContactPlayerData> = RecordCodecBuilder.create<ContactPlayerData> { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                Codec.unboundedMap(ContactID.CODEC, PokenavContact.CODEC).fieldOf("contacts").forGetter { it.contacts }
            ).apply(instance) { uuid, contacts ->
                ContactPlayerData(UUID.fromString(uuid), contacts)
            }
        }

        fun executeAndSafe(uuid: UUID, action: (ContactPlayerData) -> Boolean) {
            val data = Cobblemon.playerDataManager.getContactData(uuid)
            if (action(data)) {
                Cobblemon.playerDataManager.saveSingle(data, CobblenavDataStoreTypes.CONTACTS)
            }
        }

        fun executeAndSafe(player: ServerPlayer, action: (ContactPlayerData) -> Boolean) {
            executeAndSafe(player.uuid, action)
        }
    }

    fun onContactListUpdated() {
        uuid.getPlayer()?.let {
            SetClientPlayerDataPacket(
                type = CobblenavDataStoreTypes.CONTACTS,
                playerData = ClientContactPlayerData(contacts.mapValues { it.value.toClientContact() }.toMutableMap())
            )
        }
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientContactPlayerData(contacts.mapValues { it.value.toClientContact() }.toMutableMap())
    }
}