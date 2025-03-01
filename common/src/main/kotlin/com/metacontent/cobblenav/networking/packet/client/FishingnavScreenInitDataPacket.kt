package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.api.fishing.PokeRod
import com.cobblemon.mod.common.util.readItemStack
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeItemStack
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.WeightedBucket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class FishingnavScreenInitDataPacket(
    val buckets: List<WeightedBucket>,
    val applyBuckets: Boolean
    val pokeBall: ResourceLocation,
    val lineColor: String,
    val baitItem: ItemStack
) : CobblenavNetworkPacket<FishingnavScreenInitDataPacket> {
    companion object {
        val ID = cobblenavResource("fishingnav_screen_init_data")
        fun decode(buffer: RegistryFriendlyByteBuf) = FishingnavScreenInitDataPacket(
            buckets = buffer.readList { WeightedBucket.decode(buffer) },
            applyBuckets = buffer.readBoolean(),
            pokeBall = buffer.readResourceLocation(),
            lineColor = buffer.readString(),
            baitItem = buffer.readItemStack()
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(buckets) { buf, bucket -> bucket.encode(buf as RegistryFriendlyByteBuf) }
        buffer.writeBoolean(applyBuckets)
        buffer.writeResourceLocation(pokeBall)
        buffer.writeString(lineColor)
        buffer.writeItemStack(baitItem)
    }
}