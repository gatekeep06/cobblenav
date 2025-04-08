package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.util.interpolate
import com.metacontent.cobblenav.client.gui.util.renderSpawnDataTooltip
import com.metacontent.cobblenav.client.gui.widget.fishing.BucketViewWidget
import com.metacontent.cobblenav.client.gui.widget.fishing.WeatherWidget
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.location.SpawnDataWidget
import com.metacontent.cobblenav.networking.packet.server.RequestFishingMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestFishingnavScreenInitDataPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3f

class FishingnavScreen(
    os: PokenavOS
) : PokenavScreen(os, true, true, Component.literal("Fishing")), SpawnDataTooltipDisplayer {
    companion object {
        const val POKEMON_CHANCE = 0.85f
        const val WEATHER_WIDGET_HEIGHT = 80
        private val DAY_COLOR = RGB(117, 230, 218)
        private val NIGHT_COLOR = RGB(2, 1, 39)
    }

    override val color: Int
        get() {
            val normalizedTime = (player?.clientLevel?.dayTime ?: 0) % 24000
            return when (normalizedTime) {
                in 12040..13670 -> {
                    val progress = (normalizedTime - 12040) / 1630f
                    interpolate(DAY_COLOR, NIGHT_COLOR, progress).toColor()
                }
                in 22331..23961 -> {
                    val progress = (normalizedTime - 22331) / 1630f
                    interpolate(NIGHT_COLOR, DAY_COLOR, progress).toColor()
                }
                in 13670..22331 -> NIGHT_COLOR.toColor()
                else -> DAY_COLOR.toColor()
            }
        }

    override var hoveredSpawnData: SpawnData? = null
    lateinit var buckets: List<WeightedBucket>
    private lateinit var scrollableView: ScrollableView
    private lateinit var baseTable: TableView<SoundlessWidget>
    private lateinit var fishingTable: TableView<AbstractWidget>
    private lateinit var bucketViews: List<BucketViewWidget>

    override fun initScreen() {
        RequestFishingnavScreenInitDataPacket().sendToServer()

        baseTable = TableView(
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH,
            width = WIDTH - 2 * VERTICAL_BORDER_DEPTH,
            columns = 1,
            horizontalPadding = 0
        )
        scrollableView = ScrollableView(
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH,
            width = WIDTH - 2 * VERTICAL_BORDER_DEPTH,
            height = HEIGHT - 2 * HORIZONTAL_BORDER_DEPTH,
            child = baseTable
        ).also { addBlockableWidget(it) }
        fishingTable = TableView(
            x = 0, y = 0,
            width = baseTable.width,
            columns = 1,
            columnWidth = baseTable.columnWidth,
            horizontalPadding = 0
        )
        baseTable.add(
            WeatherWidget(
                x = 0, y = 0,
                width = WIDTH - 2 * VERTICAL_BORDER_DEPTH,
                height = WEATHER_WIDGET_HEIGHT,
                level = player?.clientLevel
            )
        )
    }

    fun receiveInitData(
        buckets: List<WeightedBucket>,
        applyBuckets: Boolean,
        pokeBall: ResourceLocation,
        lineColor: String,
        baitItem: ItemStack
    ) {
        this.buckets = buckets
        bucketViews = buckets.map {
            BucketViewWidget(
                x = 0, y = 0,
                width = fishingTable.width,
                columns = 5,
                minHeight = 100,
                bucket = it,
                verticalPadding = 2
            )
        }.also {
            fishingTable.add(it)
            baseTable.add(fishingTable)
        }

        RequestFishingMapPacket().sendToServer()
    }

    fun receiveFishingMap(fishingMap: Map<String, List<SpawnData>>) {
        fishingMap.forEach { (bucketName, spawnDatas) ->
            bucketViews.find { it.bucket.name == bucketName }?.let { view ->
                view.add(spawnDatas
                    .sortedWith { firstData, secondData -> -compareValues(firstData.spawnChance, secondData.spawnChance) }
                    .map {
                        ScrollableItemWidget(
                            child = SpawnDataWidget(
                                x = 0,
                                y = 0,
                                spawnData = it,
                                displayer = this,
                                pose = PoseType.SWIM,
                                pokemonRotation = Vector3f(0f, 270f, 0f),
                                chanceMultiplier = POKEMON_CHANCE * view.bucket.chance
                            ),
                            topEdge = screenY + HORIZONTAL_BORDER_DEPTH,
                            bottomEdge = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH
                        )
                    }
                )
            }
        }
        fishingTable.initItems()
        baseTable.initItems()
    }

    override fun renderOnFrontLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (blockWidgets || minecraft?.screen != this) return
        hoveredSpawnData?.let {
            guiGraphics.renderSpawnDataTooltip(
                spawnData = it,
                chanceMultiplier = 1f,
                mouseX = mouseX,
                mouseY = mouseY,
                x1 = screenX + VERTICAL_BORDER_DEPTH,
                y1 = screenY + HORIZONTAL_BORDER_DEPTH,
                x2 = screenX + WIDTH - VERTICAL_BORDER_DEPTH,
                y2 = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH,
                delta = delta
            )
        }
        hoveredSpawnData = null
    }

    override fun isBlockingTooltip() = blockWidgets
}