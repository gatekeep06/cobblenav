package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.fillWithOutline
import com.metacontent.cobblenav.client.gui.widget.location.SpawnDataWidget
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollThumbWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.location.BucketSelectorWidget
import com.metacontent.cobblenav.client.gui.widget.location.LocationInfoWidget
import com.metacontent.cobblenav.networking.packet.server.RequestLocationScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestSpawnMapPacket
import com.metacontent.cobblenav.networking.packet.server.SavePreferencesPacket
import com.metacontent.cobblenav.client.gui.util.Sorting
import com.metacontent.cobblenav.client.gui.util.renderSpawnDataTooltip
import com.metacontent.cobblenav.client.gui.widget.StatusBarWidget
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialMenuState
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialPopupMenu
import com.metacontent.cobblenav.util.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import kotlin.math.max
import kotlin.math.min

class LocationScreen(
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean= false
) : PokenavScreen(makeOpeningSound, animateOpening, Component.literal("Location")) {
    companion object {
        val LOADING = cobblenavResource("textures/gui/loading_animation.png")
        const val ANIMATION_SHEET_WIDTH: Int = 144
        const val FRAME_WIDTH: Int = 18
        const val FRAME_HEIGHT: Int = 22
        const val LOADING_LOOP_DURATION: Float = 10f
        const val BUTTON_BLOCK_SPACE: Int = 10
        const val BUTTON_SPACE: Int = 5
        const val BUTTON_WIDTH: Int = 15
        const val BUTTON_HEIGHT: Int = 16
        val VIEW_BACKGROUND_COLOR = FastColor.ARGB32.color(255, 125, 190, 164)
        val VIEW_OUTLINE_COLOR = FastColor.ARGB32.color(255, 84, 168, 134)
        val SORT_ASCENDING = cobblenavResource("textures/gui/button/sort_button_ascending.png")
        val SORT_DESCENDING = cobblenavResource("textures/gui/button/sort_button_descending.png")
        val REFRESH = cobblenavResource("textures/gui/button/refresh_button.png")
    }
    var viewX = 0
    var viewY = 0
    val viewWidth = WIDTH - 2 * (VERTICAL_BORDER_DEPTH + 5)
    val viewHeight = HEIGHT - 2 * (HORIZONTAL_BORDER_DEPTH + 18)
    override val color = FastColor.ARGB32.color(255, 63, 126, 101)
    private val spawnDataMap = mutableMapOf<String, List<SpawnData>>()
    lateinit var buckets: List<String>
    var bucketIndex = -1
        set(value) {
            field = max(min(value, buckets.size - 1), 0)
            onBucketChange()
        }
    var currentBucket: String
        get() = buckets[bucketIndex]
        set(value) { bucketIndex = buckets.indexOf(value) }
    private var sorting = Sorting.ASCENDING
        set(value) {
            field = value
            onSortingChange()
        }
    private lateinit var biome: String
    private var loading = false
    private val timer = Timer(LOADING_LOOP_DURATION, true)
    private val frameAmount: Int = ANIMATION_SHEET_WIDTH / FRAME_WIDTH
    var hoveredSpawnData: SpawnData? = null
    private lateinit var tableView: TableView<ScrollableItemWidget<SpawnDataWidget>>
    private lateinit var scrollableView: ScrollableView
    private lateinit var bucketSelector: BucketSelectorWidget
    private lateinit var sortButton: IconButton
    private lateinit var refreshButton: IconButton

    override fun initScreen() {
        viewX = screenX + VERTICAL_BORDER_DEPTH + 5
        viewY = screenY + HORIZONTAL_BORDER_DEPTH + 20

        RadialPopupMenu(
            this,
            screenX + (WIDTH - RadialMenuState.MENU_DIAMETER) / 2,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - RadialMenuState.MENU_DIAMETER / 2
        ).also { addUnblockableWidget(it) }

        StatusBarWidget(
            screenX + WIDTH - VERTICAL_BORDER_DEPTH - StatusBarWidget.WIDTH - 2,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - StatusBarWidget.HEIGHT
        ).also { addUnblockableWidget(it) }

        sortButton = IconButton(
            pX = viewX + BucketSelectorWidget.WIDTH + BUTTON_BLOCK_SPACE,
            pY = viewY - (BucketSelectorWidget.HEIGHT + BUTTON_HEIGHT) / 2,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            disabled = true,
            action = { this.sorting = if (this.sorting == Sorting.ASCENDING) Sorting.DESCENDING else Sorting.ASCENDING },
            texture = null,
        ).also { addBlockableWidget(it) }

        RequestLocationScreenInitDataPacket().sendToServer()

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { changeScreen(MainScreen()) }
        ).also { addBlockableWidget(it) }

        tableView = TableView(
            viewX + 1, viewY + 1,
            viewWidth - 2 - ScrollThumbWidget.WIDTH, 5,
            verticalPadding = 5,
            columnWidth = SpawnDataWidget.WIDTH,
            rowHeight = SpawnDataWidget.HEIGHT
        )
        scrollableView = ScrollableView(
            tableView.x,
            tableView.y,
            tableView.width + 2,
            viewHeight - 2,
            child = tableView
        ).also { addBlockableWidget(it) }

        refreshButton = IconButton(
            pX = viewX + BucketSelectorWidget.WIDTH + BUTTON_BLOCK_SPACE + BUTTON_WIDTH + BUTTON_SPACE,
            pY = viewY - (BucketSelectorWidget.HEIGHT + BUTTON_HEIGHT) / 2,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            disabled = loading,
            action = {
                scrollableView.reset()
                requestSpawnData()
            },
            texture = REFRESH
        ).also { addBlockableWidget(it) }
    }

    fun receiveInitData(buckets: List<String>, biome: String, bucketIndex: Int, sorting: Sorting) {
        this.buckets = buckets
        this.bucketIndex = bucketIndex
        bucketSelector = BucketSelectorWidget(
            viewX, viewY - BucketSelectorWidget.HEIGHT,
            this
        ).also { addBlockableWidget(it) }

        this.biome = biome
        LocationInfoWidget(
            x = viewX + viewWidth - LocationInfoWidget.WIDTH,
            y = viewY - LocationInfoWidget.HEIGHT,
            biome = this.biome
        ).also { addBlockableWidget(it) }

        this.sorting = sorting
        sortButton.disabled = false
    }

    fun receiveSpawnData(spawnDataList: List<SpawnData>) {
        this.spawnDataMap[currentBucket] = spawnDataList
//        spawnDataList.forEach { (renderablePokemon, fl, bool, biome) -> player?.sendSystemMessage(Component.literal(renderablePokemon.species.name + ": " + fl + ", " + bool + ", " + biome.toString())) }
        createSpawnDataWidgets(spawnDataList)
        loading = false
        refreshButton.disabled = false
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        guiGraphics.fillWithOutline(
            viewX, viewY,
            viewX + viewWidth,
            viewY + viewHeight,
            VIEW_BACKGROUND_COLOR,
            VIEW_OUTLINE_COLOR
        )
        if (loading) {
            poseStack.pushPose()
            poseStack.translate(0f, 0f, 400f)
            renderLoadingAnimation(guiGraphics.pose(), delta)
            poseStack.popPose()
        }
    }

    override fun renderOnFrontLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        hoveredSpawnData?.let {
            guiGraphics.renderSpawnDataTooltip(
                spawnData = it,
                mouseX = mouseX,
                mouseY = mouseY,
                x1 = viewX,
                y1 = viewY,
                x2 = viewX + viewWidth,
                y2 = viewY + viewHeight
            )
        }
        hoveredSpawnData = null
    }

    private fun savePreferences() {
        SavePreferencesPacket(bucketIndex, sorting).sendToServer()
    }

    private fun requestSpawnData() {
        loading = true
        refreshButton.disabled = true
        RequestSpawnMapPacket(currentBucket).sendToServer()
    }

    override fun onScreenChange() {
        savePreferences()
    }

    private fun renderLoadingAnimation(poseStack: PoseStack, delta: Float) {
        timer.tick(delta)
        blitk(
            poseStack, LOADING,
            screenX + (WIDTH - FRAME_WIDTH) / 2,
            screenY + (HEIGHT - FRAME_HEIGHT) / 2,
            width = FRAME_WIDTH,
            height = FRAME_HEIGHT,
            textureWidth = ANIMATION_SHEET_WIDTH,
            uOffset = FRAME_WIDTH * ((frameAmount - 1) * timer.getProgress()).toInt()
        )
    }

    private fun onBucketChange() {
        scrollableView.reset()
        val spawnDataList = spawnDataMap[currentBucket]
        if (spawnDataList == null) {
            requestSpawnData()
            return
        }
        createSpawnDataWidgets(spawnDataList)
    }

    private fun onSortingChange() {
        sortButton.texture = if (sorting == Sorting.ASCENDING) SORT_ASCENDING else SORT_DESCENDING
        tableView.resort(sorting) { widget -> widget.child.spawnData.spawnChance }
    }

    private fun createSpawnDataWidgets(spawnDataList: List<SpawnData>) {
        tableView.clear()
        val spawnDataWidgets = spawnDataList
            .sortedWith { firstData, secondData -> compareValues(firstData.spawnChance, secondData.spawnChance) * sorting.multiplier }
            .map {
                ScrollableItemWidget(
                    SpawnDataWidget(0, 0, it, this),
                    screenY + HORIZONTAL_BORDER_DEPTH + 16,
                    screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - 15
                )
            }
        tableView.add(spawnDataWidgets)
    }
}