package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.metacontent.cobblenav.api.contact.ContactID
import com.metacontent.cobblenav.api.contact.PokenavContact
import com.metacontent.cobblenav.storage.client.ClientContactPlayerData
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
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
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientContactPlayerData(contacts.values.map { it.toClientContact() }.toMutableList())
    }
}