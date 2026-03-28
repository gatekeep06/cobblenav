package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.net.messages.client.data.DataRegistrySyncPacket
import com.cobblemon.mod.common.util.readIdentifier
import com.cobblemon.mod.common.util.writeIdentifier
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

class EvYieldSyncPacket(speciesToEvYield: Collection<Pair<ResourceLocation, Map<Stats, Int>>>) :
    DataRegistrySyncPacket<Pair<ResourceLocation, Map<Stats, Int>>, EvYieldSyncPacket>(speciesToEvYield) {
    companion object {
        val ID = cobblenavResource("ev_yield_sync")
        fun decode(buffer: RegistryFriendlyByteBuf) = EvYieldSyncPacket(emptyList()).apply {
            val size = buffer.readInt()
            val newBuffer = RegistryFriendlyByteBuf(buffer.readBytes(size), buffer.registryAccess())
            this.buffer = newBuffer
        }
    }

    override val id = ID

    override fun decodeEntry(buffer: RegistryFriendlyByteBuf): Pair<ResourceLocation, Map<Stats, Int>>? {
        return try {
            buffer.readIdentifier() to buffer.readMap(
                { it.readEnum(Stats::class.java) },
                { it.readInt() }
            )
        } catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
            null
        }
    }

    override fun synchronizeDecoded(entries: Collection<Pair<ResourceLocation, Map<Stats, Int>>>) {
        entries.forEach { (id, evYield) ->
            PokemonSpecies.getByIdentifier(id)?.evYield?.putAll(evYield)
        }
    }

    override fun encodeEntry(buffer: RegistryFriendlyByteBuf, entry: Pair<ResourceLocation, Map<Stats, Int>>) {
        try {
            buffer.writeIdentifier(entry.first)
            buffer.writeMap(
                entry.second,
                { _, stat -> buffer.writeEnum(stat) },
                { _, value -> buffer.writeInt(value) }
            )
        } catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
        }
    }
}