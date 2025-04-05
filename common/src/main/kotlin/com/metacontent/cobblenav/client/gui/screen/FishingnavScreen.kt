package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.gui.widget.fishing.WeatherWidget
import com.metacontent.cobblenav.networking.packet.server.RequestFishingMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestFishingnavScreenInitDataPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor
import net.minecraft.world.item.ItemStack

class FishingnavScreen(
    os: PokenavOS
) : PokenavScreen(os, true, true, Component.literal("Fishing")) {
    private val dayColor = Vec3i(117, 230, 218)
    private val nightColor = Vec3i(2, 1, 39)
    override val color: Int
        get() {
            val normalizedTime = (player?.clientLevel?.dayTime ?: 0) % 24000
            return when (normalizedTime) {
                in 12040..13670 -> {
                    val progress = (normalizedTime - 12040) / 1630f
                    interpolate(dayColor, nightColor, progress).toColor()
                }
                in 22331..23961 -> {
                    val progress = (normalizedTime - 22331) / 1630f
                    interpolate(nightColor, dayColor, progress).toColor()
                }
                in 13670..22331 -> nightColor.toColor()
                else -> dayColor.toColor()
            }
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

    private fun Vec3i.toColor(opacity: Int = 255) = FastColor.ARGB32.color(opacity, x, y, z)

    private fun interpolate(start: Vec3i, end: Vec3i, progress: Float) : Vec3i {
        return Vec3i(
            interpolateChannel(start.x, end.x, progress),
            interpolateChannel(start.y, end.y, progress),
            interpolateChannel(start.z, end.z, progress)
        )
    }

    private fun interpolateChannel(start: Int, end: Int, progress: Float): Int {
        return (start + (end - start) * progress).toInt().coerceIn(0..255)
    }
}