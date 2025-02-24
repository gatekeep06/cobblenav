package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class SpawnMapPacket(
    val bucketName: String,
    val spawnDataList: List<SpawnData>
) : CobblenavNetworkPacket<SpawnMapPacket> {
    companion object {
        val ID = cobblenavResource("spawn_map")
        fun decode(buffer: RegistryFriendlyByteBuf) = SpawnMapPacket(
            buffer.readString(),
            buffer.readList { SpawnData.decode(it as RegistryFriendlyByteBuf) }
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(bucketName)
        buffer.writeCollection(spawnDataList) { byteBuf, spawnData ->
            spawnData.encode(byteBuf as RegistryFriendlyByteBuf)
        }
    }
}