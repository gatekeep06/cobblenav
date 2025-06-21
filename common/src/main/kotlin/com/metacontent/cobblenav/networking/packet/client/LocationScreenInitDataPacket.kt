package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.gui.util.Sorting
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.WeightedBucket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class LocationScreenInitDataPacket(
    val buckets: List<WeightedBucket>,
    val biome: String
) : CobblenavNetworkPacket<LocationScreenInitDataPacket> {
    companion object {
        val ID = cobblenavResource("location_screen_init_data")
        fun decode(buffer: RegistryFriendlyByteBuf) = LocationScreenInitDataPacket(
            buffer.readList { WeightedBucket.decode(buffer) },
            buffer.readString()
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(buckets) { buf, bucket -> bucket.encode(buf as RegistryFriendlyByteBuf) }
        buffer.writeString(biome)
    }
}