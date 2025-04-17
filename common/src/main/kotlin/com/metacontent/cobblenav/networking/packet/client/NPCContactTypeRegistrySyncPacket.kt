package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.net.messages.client.data.DataRegistrySyncPacket
import com.metacontent.cobblenav.api.contact.type.NPCContactType
import com.metacontent.cobblenav.api.contact.type.NPCContactTypes
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class NPCContactTypeRegistrySyncPacket(
    npcContactTypes: Collection<NPCContactType>
) : DataRegistrySyncPacket<NPCContactType, NPCContactTypeRegistrySyncPacket>(npcContactTypes) {
    companion object {
        val ID = cobblenavResource("npc_contact_type_sync")
        fun decode(buffer: RegistryFriendlyByteBuf) = NPCContactTypeRegistrySyncPacket(emptyList()).apply {
            val size = buffer.readInt()
            val decodedBuffer = RegistryFriendlyByteBuf(buffer.readBytes(size), buffer.registryAccess())
            this.buffer = decodedBuffer
        }
    }

    override val id = ID

    override fun encodeEntry(buffer: RegistryFriendlyByteBuf, entry: NPCContactType) {
        entry.encode(buffer)
    }

    override fun decodeEntry(buffer: RegistryFriendlyByteBuf) = NPCContactType.decode(buffer)

    override fun synchronizeDecoded(entries: Collection<NPCContactType>) {
        NPCContactTypes.reload(entries.associateBy { it.npcClass })
    }
}