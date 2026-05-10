package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

interface CatalogueUpdatePacket<T : CatalogueUpdatePacket<T>> : CobblenavNetworkPacket<T>

class AddCatalogueEntriesPacket(
    val added: Map<String, List<SpawnData>>
) : CatalogueUpdatePacket<AddCatalogueEntriesPacket> {
    companion object {
        val ID = cobblenavResource("catalogue_added")
        fun decode(buffer: RegistryFriendlyByteBuf) = AddCatalogueEntriesPacket(
            added = buffer.readMap(
                { it.readString() },
                { it.readList { buf -> SpawnData.decode(buf as RegistryFriendlyByteBuf) } }
            )
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeMap(
            added,
            { buf, key -> buf.writeString(key) },
            { buf, value -> buf.writeCollection(value) { b, data -> data.encode(b as RegistryFriendlyByteBuf) } }
        )
    }
}

class RemoveCatalogueEntriesPacket(
    val removed: Set<String>
) : CatalogueUpdatePacket<RemoveCatalogueEntriesPacket> {
    companion object {
        val ID = cobblenavResource("catalogue_removed")
        fun decode(buffer: RegistryFriendlyByteBuf) = RemoveCatalogueEntriesPacket(
            removed = buffer.readList { it.readString() }.toSet()
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(removed) { buf, string -> buf.writeString(string) }
    }
}