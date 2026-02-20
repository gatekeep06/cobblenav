package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.spawndata.CheckedSpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class SpawnMapPacket(
    val weightedBucket: WeightedBucket,
    val spawnDataList: List<CheckedSpawnData>
) : CobblenavNetworkPacket<SpawnMapPacket> {
    companion object {
        val ID = cobblenavResource("spawn_map")
        fun decode(buffer: RegistryFriendlyByteBuf) = SpawnMapPacket(
            WeightedBucket.decode(buffer),
            buffer.readList { CheckedSpawnData.decode(it as RegistryFriendlyByteBuf) }
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        weightedBucket.encode(buffer)
        buffer.writeCollection(spawnDataList) { byteBuf, spawnData ->
            spawnData.encode(byteBuf as RegistryFriendlyByteBuf)
        }
    }
}