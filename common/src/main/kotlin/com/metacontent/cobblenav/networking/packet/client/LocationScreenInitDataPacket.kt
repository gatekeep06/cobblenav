package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.gui.util.Sorting
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class LocationScreenInitDataPacket(
    val buckets: List<String>,
    val biome: String,
    val bucketIndex: Int,
    val sorting: Sorting
) : NetworkPacket<LocationScreenInitDataPacket> {
    companion object {
        val ID = cobblenavResource("location_screen_init_data")
        fun decode(buffer: RegistryFriendlyByteBuf) = LocationScreenInitDataPacket(
            buffer.readList { it.readString() },
            buffer.readString(),
            buffer.readInt(),
            buffer.readEnum(Sorting::class.java)
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(buckets) { byteBuf, string -> byteBuf.writeString(string) }
        buffer.writeString(biome)
        buffer.writeInt(bucketIndex)
        buffer.writeEnum(sorting)
    }
}