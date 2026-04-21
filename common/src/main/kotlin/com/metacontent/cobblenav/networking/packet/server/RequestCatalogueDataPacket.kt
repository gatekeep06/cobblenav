package com.metacontent.cobblenav.networking.packet.server

import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class RequestCatalogueDataPacket(
    val spawnIds: List<String>? = null
) : CobblenavNetworkPacket<RequestCatalogueDataPacket> {
    companion object {
        val ID = cobblenavResource("request_catalogue_data")
        fun decode(buffer: RegistryFriendlyByteBuf) = RequestCatalogueDataPacket(
            spawnIds = buffer.readNullable { buf -> buf.readList { it.readString() } }
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeNullable(spawnIds) { buf, ids ->
            buf.writeCollection(ids) { buf, id -> buf.writeString(id) }
        }
    }
}