package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class CataloguePacket(val spawnDataList: List<SpawnData>) : CobblenavNetworkPacket<CataloguePacket> {
    companion object {
        val ID = cobblenavResource("catalogue")

        fun decode(buffer: RegistryFriendlyByteBuf) = CataloguePacket(
            spawnDataList = buffer.readList { buf -> SpawnData.decode(buf as RegistryFriendlyByteBuf) }
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(spawnDataList) { buf, data -> data.encode(buf as RegistryFriendlyByteBuf) }
    }
}