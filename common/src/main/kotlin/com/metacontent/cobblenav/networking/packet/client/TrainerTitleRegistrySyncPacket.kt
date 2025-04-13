package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.net.messages.client.data.DataRegistrySyncPacket
import com.metacontent.cobblenav.api.contact.title.TrainerTitle
import com.metacontent.cobblenav.api.contact.title.TrainerTitles
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class TrainerTitleRegistrySyncPacket(
    titles: Collection<TrainerTitle>
) : DataRegistrySyncPacket<TrainerTitle, TrainerTitleRegistrySyncPacket>(titles) {
    companion object {
        val ID = cobblenavResource("title_sync")
        fun decode(buffer: RegistryFriendlyByteBuf) = TrainerTitleRegistrySyncPacket(emptyList()).apply {
            val size = buffer.readInt()
            val decodedBuffer = RegistryFriendlyByteBuf(buffer.readBytes(size), buffer.registryAccess())
            this.buffer = decodedBuffer
        }
    }

    override val id = ID

    override fun encodeEntry(buffer: RegistryFriendlyByteBuf, entry: TrainerTitle) {
        entry.encode(buffer)
    }

    override fun decodeEntry(buffer: RegistryFriendlyByteBuf) = TrainerTitle.decode(buffer)

    override fun synchronizeDecoded(entries: Collection<TrainerTitle>) {
        TrainerTitles.reload(entries.associateBy { it.id })
    }
}