package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class CatalogueDataPacket(
    val catalogueData: List<SpawnData>
) : CobblenavNetworkPacket<CatalogueDataPacket> {
    companion object {
        val ID = cobblenavResource("catalogue_data")
        fun decode(buffer: RegistryFriendlyByteBuf) = CatalogueDataPacket(
            catalogueData = buffer.readList { SpawnData.decode(it as RegistryFriendlyByteBuf) })
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(catalogueData) { buf, data -> data.encode(buf as RegistryFriendlyByteBuf) }
    }
}