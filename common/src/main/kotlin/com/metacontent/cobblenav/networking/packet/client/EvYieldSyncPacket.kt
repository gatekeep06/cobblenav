package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.net.messages.client.data.DataRegistrySyncPacket
import com.cobblemon.mod.common.util.readIdentifier
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeIdentifier
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.cobblenavResource
import com.metacontent.cobblenav.util.setEvYield
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

class EvYieldSyncPacket(evYieldData: Collection<EvYieldDataEntry>) :
    DataRegistrySyncPacket<EvYieldDataEntry, EvYieldSyncPacket>(evYieldData) {
    companion object {
        val ID = cobblenavResource("ev_yield_sync")
        fun decode(buffer: RegistryFriendlyByteBuf) = EvYieldSyncPacket(emptyList()).apply {
            val size = buffer.readInt()
            val newBuffer = RegistryFriendlyByteBuf(buffer.readBytes(size), buffer.registryAccess())
            this.buffer = newBuffer
        }
    }

    override val id = ID

    override fun decodeEntry(buffer: RegistryFriendlyByteBuf): EvYieldDataEntry? {
        return try {
            EvYieldDataEntry.decode(buffer)
        } catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
            null
        }
    }

    override fun synchronizeDecoded(entries: Collection<EvYieldDataEntry>) {
        entries.forEach { (id, speciesEvYield, formToEvYield) ->
            val species = PokemonSpecies.getByIdentifier(id) ?: return@forEach
            species.evYield.putAll(speciesEvYield)
            formToEvYield.forEach { (formName, stats) ->
                species.forms.find { it.name == formName }?.setEvYield(stats?.toMutableMap())
            }
        }
    }

    override fun encodeEntry(buffer: RegistryFriendlyByteBuf, entry: EvYieldDataEntry) {
        try {
            entry.encode(buffer)
        } catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
        }
    }
}

data class EvYieldDataEntry(
    val speciesId: ResourceLocation,
    val speciesEvYield: Map<Stats, Int>,
    val formToEvYield: Map<String, Map<Stats, Int>?>
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf): EvYieldDataEntry = EvYieldDataEntry(
            speciesId = buffer.readIdentifier(),
            speciesEvYield = buffer.readMap(
                { buffer.readEnum(Stats::class.java) },
                { buffer.readInt() }
            ),
            formToEvYield = buffer.readMap(
                { buffer.readString() },
                {
                    buffer.readNullable {
                        buffer.readMap(
                            { buffer.readEnum(Stats::class.java) },
                            { buffer.readInt() }
                        )
                    }
                }
            )
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeIdentifier(speciesId)
        buffer.writeMap(
            speciesEvYield,
            { _, stat -> buffer.writeEnum(stat) },
            { _, value -> buffer.writeInt(value) }
        )
        buffer.writeMap(
            formToEvYield,
            { _, form -> buffer.writeString(form) },
            { _, map ->
                buffer.writeNullable(map) { _, m ->
                    buffer.writeMap(
                        m,
                        { _, stat -> buffer.writeEnum(stat) },
                        { _, value -> buffer.writeInt(value) }
                    )
                }
            }
        )
    }
}