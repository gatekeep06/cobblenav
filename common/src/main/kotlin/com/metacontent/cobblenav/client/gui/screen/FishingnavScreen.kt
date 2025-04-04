package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.client.gui.widget.fishing.WeatherWidget
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
    override val color
        get() = if (((player?.clientLevel?.dayTime ?: 0) % 24000) in 13000..23000) {
            FastColor.ARGB32.color(255, 2, 1, 39)
        }
        else {
            FastColor.ARGB32.color(255, 117, 230, 218)
        }

    lateinit var buckets: List<WeightedBucket>
    private lateinit var weather: WeatherWidget

    override fun initScreen() {
        RequestFishingnavScreenInitDataPacket().sendToServer()

        weather = WeatherWidget(
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH,
            width = WIDTH - 2 * VERTICAL_BORDER_DEPTH,
            height = 80
        ).also { addBlockableWidget(it) }
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