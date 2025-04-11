package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.net.messages.client.data.DataRegistrySyncPacket
import com.cobblemon.mod.common.util.*
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

class LabelSyncPacket(speciesToLabels: Collection<Pair<ResourceLocation, HashSet<String>>>) : DataRegistrySyncPacket<Pair<ResourceLocation, HashSet<String>>, LabelSyncPacket>(speciesToLabels) {
    companion object {
        val ID = cobblenavResource("label_sync")
        fun decode(buffer: RegistryFriendlyByteBuf) = LabelSyncPacket(emptyList()).apply {
            val size = buffer.readInt()
            val newBuffer = RegistryFriendlyByteBuf(buffer.readBytes(size), buffer.registryAccess())
            this.buffer = newBuffer
        }
    }

    override val id = ID

    override fun decodeEntry(buffer: RegistryFriendlyByteBuf): Pair<ResourceLocation, HashSet<String>>? {
        return try {
            buffer.readIdentifier() to buffer.readList { it.readString() }.toHashSet()
        }
        catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
            null
        }
    }

    override fun synchronizeDecoded(entries: Collection<Pair<ResourceLocation, HashSet<String>>>) {
        entries.forEach { pair ->
            PokemonSpecies.getByIdentifier(pair.first)?.labels?.addAll(pair.second)
        }
    }

    override fun encodeEntry(buffer: RegistryFriendlyByteBuf, entry: Pair<ResourceLocation, HashSet<String>>) {
        try {
            buffer.writeIdentifier(entry.first)
            buffer.writeCollection(entry.second) { byteBuf, s -> byteBuf.writeString(s) }
        }
        catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
        }
    }
}