package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.networking.packet.server.RequestFishingMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestFishingnavScreenInitDataPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor
import net.minecraft.world.item.ItemStack

class FishingnavScreen(
    os: PokenavOS
) : PokenavScreen(os, true, true, Component.literal("Fishing")) {
    override val color = FastColor.ARGB32.color(255, 117, 230, 218)

    lateinit var buckets: List<WeightedBucket>

    override fun initScreen() {
        RequestFishingnavScreenInitDataPacket().sendToServer()
    }

    fun receiveInitData(
        buckets: List<WeightedBucket>,
        applyBuckets: Boolean,
        pokeBall: ResourceLocation,
        lineColor: String,
        baitItem: ItemStack
    ) {
        this.buckets = buckets
        RequestFishingMapPacket().sendToServer()
    }

    fun receiveFishingMap(fishingMap: Map<String, List<SpawnData>>) {

    }
}