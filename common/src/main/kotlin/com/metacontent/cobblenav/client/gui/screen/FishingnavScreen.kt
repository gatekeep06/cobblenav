package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.util.dayCycleColor
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.renderSpawnDataTooltip
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.fishing.BucketViewWidget
import com.metacontent.cobblenav.client.gui.widget.fishing.FishingContextWidget
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataWidget
import com.metacontent.cobblenav.networking.packet.server.RequestFishingMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestFishingnavScreenInitDataPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class FishingnavScreen(
    os: PokenavOS
) : PokenavScreen(os, true, true, Component.literal("Fishing")), SpawnDataDisplayer {
    companion object {
        const val POKEMON_CHANCE = 0.85f
        const val WEATHER_WIDGET_HEIGHT = 40
        const val BUCKET_VIEW_MIN_HEIGHT = 100
        const val PANEL_WIDTH = 20
        const val DEPTH_WIDTH = 9
        const val DEPTH_HEIGHT = 12
        const val SWITCH_OFF_SIZE = 15
        const val NAV_BUTTON_WIDTH = 11
        const val NAV_BUTTON_HEIGHT = 8
        const val BUTTON_WIDTH = 15
        const val BUTTON_HEIGHT = 16
        const val BUTTON_GAP = 14
        private val DAY_COLOR = RGB(115, 215, 255)
        private val NIGHT_COLOR = RGB(2, 1, 39)
        val PANEL = gui("fishing/panel")
        val DEPTH = gui("fishing/depth_symbol")
        val SWITCH_OFF = gui("radialmenu/switch_off")
        val UP = gui("button/up_button")
        val DOWN = gui("button/down_button")
        val REFRESH = gui("button/refresh_button")
    }

    override val color: Int
        get() = dayCycleColor(player?.clientLevel?.dayTime ?: 0L, DAY_COLOR, NIGHT_COLOR).toColor()

    var loading = false
    override var displayedData: List<SpawnData>? = null
    override var hoveredData: SpawnData? = null
    override var selectedData: SpawnData? = null
    lateinit var buckets: List<WeightedBucket>
    private lateinit var fishingContextWidget: FishingContextWidget
    private lateinit var scrollableView: ScrollableView
    private lateinit var baseTable: TableView<SoundlessWidget>
    private lateinit var fishingTable: TableView<AbstractWidget>
    private lateinit var bucketViews: List<BucketViewWidget>
    private lateinit var refreshButton: IconButton

    override fun initScreen() {
        RequestFishingnavScreenInitDataPacket().sendToServer()

        baseTable = TableView(
            x = screenX + VERTICAL_BORDER_DEPTH + PANEL_WIDTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH,
            width = WIDTH - 2 * VERTICAL_BORDER_DEPTH - PANEL_WIDTH,
            columns = 1,
            horizontalGap = 0f
        )
        scrollableView = ScrollableView(
            x = screenX + VERTICAL_BORDER_DEPTH + PANEL_WIDTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH,
            width = WIDTH - 2 * VERTICAL_BORDER_DEPTH - PANEL_WIDTH,
            height = HEIGHT - 2 * HORIZONTAL_BORDER_DEPTH,
            child = baseTable
        ).also { addBlockableWidget(it) }
        fishingTable = TableView(
            x = 0, y = 0,
            width = baseTable.width,
            columns = 1,
            columnWidth = baseTable.columnWidth,
            horizontalGap = 0f
        )
        fishingContextWidget = FishingContextWidget(
            x = 0, y = 0,
            width = baseTable.width,
            height = WEATHER_WIDGET_HEIGHT,
            level = player?.clientLevel
        )
        baseTable.add(fishingContextWidget)

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH + 2,
            pY = screenY + HORIZONTAL_BORDER_DEPTH + 2,
            pWidth = SWITCH_OFF_SIZE,
            pHeight = SWITCH_OFF_SIZE,
            texture = SWITCH_OFF,
            action = { this.onClose() }
        ).also { addBlockableWidget(it) }

        refreshButton = IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH + 2,
            pY = screenY + HEIGHT / 2 - 2 * BUTTON_GAP - NAV_BUTTON_HEIGHT - BUTTON_HEIGHT,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            disabled = loading,
            action = {
                scrollableView.reset()
                bucketViews.forEach { it.clear() }
                requestFishingData()
            },
            texture = REFRESH
        ).also { addBlockableWidget(it) }
    }

    fun receiveInitData(
        buckets: List<WeightedBucket>,
        pokeBall: ResourceLocation,
        lineColor: String,
        baitItem: ItemStack
    ) {
        this.buckets = buckets
        bucketViews = buckets.mapIndexed { index, bucket ->
            BucketViewWidget(
                x = 0, y = 0,
                width = fishingTable.width,
                columns = 5,
                minHeight = BUCKET_VIEW_MIN_HEIGHT,
                depthProgress = index / buckets.size.toFloat(),
                bucket = bucket,
                verticalPadding = 4f
            )
        }.also {
            fishingTable.add(it)
            baseTable.add(fishingTable)
        }

        fishingContextWidget.lineColor = lineColor.toIntOrNull()
        fishingContextWidget.pokeBallStack = ItemStack(BuiltInRegistries.ITEM.get(pokeBall))
        fishingContextWidget.baitStack = baitItem

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH + 4,
            pY = screenY + HEIGHT / 2 - BUTTON_GAP - NAV_BUTTON_HEIGHT,
            pWidth = NAV_BUTTON_WIDTH,
            pHeight = NAV_BUTTON_HEIGHT,
            texture = UP,
            action = {
                if (scrollableView.scrolled <= fishingContextWidget.height) {
                    scrollableView.scrolled = 0
                    return@IconButton
                }
                var heightSum = fishingContextWidget.height
                bucketViews.firstOrNull {
                    if (scrollableView.scrolled > heightSum + it.height) {
                        heightSum += it.height
                        return@firstOrNull false
                    }
                    return@firstOrNull true
                }
                scrollableView.scrolled = heightSum
            }
        ).also { addBlockableWidget(it) }
        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH + 4,
            pY = screenY + HEIGHT / 2 + BUTTON_GAP,
            pWidth = NAV_BUTTON_WIDTH,
            pHeight = NAV_BUTTON_HEIGHT,
            texture = DOWN,
            action = {
                var heightSum = fishingContextWidget.height
                bucketViews.firstOrNull {
                    if (scrollableView.scrolled >= heightSum) {
                        heightSum += it.height
                        return@firstOrNull false
                    }
                    return@firstOrNull true
                }
                scrollableView.scrolled = heightSum
            }
        ).also { addBlockableWidget(it) }

        requestFishingData()
    }

    private fun requestFishingData() {
        loading = true
        refreshButton.disabled = true
        RequestFishingMapPacket().sendToServer()
    }

    fun receiveFishingMap(fishingMap: Map<String, List<SpawnData>>) {
        displayedData = fishingMap.flatMap { it.value }
        fishingMap.forEach { (bucketName, spawnDatas) ->
            bucketViews.find { it.bucket.name == bucketName }?.let { view ->
                view.add(spawnDatas
                    .sortedWith { firstData, secondData ->
                        -compareValues(
                            firstData.spawnChance,
                            secondData.spawnChance
                        )
                    }
                    .map {
                        it.chanceMultiplier = view.bucket.chance * POKEMON_CHANCE
                        ScrollableItemWidget(
                            child = SpawnDataWidget(
                                x = 0,
                                y = 0,
                                spawnData = it,
                                displayer = this
                            ),
                            topEdge = screenY + HORIZONTAL_BORDER_DEPTH,
                            bottomEdge = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH
                        )
                    }
                )
            }
        }
        loading = false
        refreshButton.disabled = false
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()

        blitk(
            matrixStack = poseStack,
            texture = PANEL,
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH,
            width = PANEL_WIDTH,
            height = HEIGHT - 2 * HORIZONTAL_BORDER_DEPTH
        )

        blitk(
            matrixStack = poseStack,
            texture = DEPTH,
            x = screenX + VERTICAL_BORDER_DEPTH + (PANEL_WIDTH - DEPTH_WIDTH) / 2,
            y = screenY + (HEIGHT - DEPTH_HEIGHT) / 2,
            width = DEPTH_WIDTH,
            height = DEPTH_HEIGHT,
            alpha = 0.6f
        )
    }

    override fun renderOnFrontLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (blockWidgets || minecraft?.screen != this) return
        hoveredData?.let {
            guiGraphics.renderSpawnDataTooltip(
                spawnData = it,
                chanceMultiplier = it.chanceMultiplier,
                mouseX = mouseX,
                mouseY = mouseY,
                x1 = screenX + VERTICAL_BORDER_DEPTH,
                y1 = screenY + HORIZONTAL_BORDER_DEPTH,
                x2 = screenX + WIDTH - VERTICAL_BORDER_DEPTH,
                y2 = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH,
                delta = delta
            )
        }
        hoveredData = null
    }

    override fun isBlockingTooltip() = blockWidgets

    override fun selectedCanBeTracked() = false
}