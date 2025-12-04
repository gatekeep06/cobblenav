package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.*
import com.metacontent.cobblenav.client.gui.widget.ContextMenuWidget
import com.metacontent.cobblenav.client.gui.widget.StatusBarWidget
import com.metacontent.cobblenav.client.gui.widget.button.CheckBox
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.location.BucketSelectorWidget
import com.metacontent.cobblenav.client.gui.widget.location.LocationInfoWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataDetailsWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataWidget
import com.metacontent.cobblenav.client.settings.PokenavPreferences
import com.metacontent.cobblenav.networking.packet.server.RequestLocationScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestSpawnMapPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import net.minecraft.world.phys.AABB
import org.joml.Vector3d
import kotlin.math.max
import kotlin.math.min

class LocationScreen(
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Location")), SpawnDataDisplayer {
    companion object {
        val LOADING = gui("location/loading_animation")
        const val ANIMATION_SHEET_WIDTH = 144
        const val FRAME_WIDTH = 18
        const val FRAME_HEIGHT = 22
        const val LOADING_LOOP_DURATION = 10f
        const val BUTTON_BLOCK_SPACE = 10
        const val BUTTON_SPACE = 5
        const val BUTTON_WIDTH = 15
        const val BUTTON_HEIGHT = 16
        const val CHECK_BOX_WIDTH = 100
        const val CHECK_BOX_HEIGHT = 8
        const val CHECK_BOX_OFFSET = 4
        const val TABLE_MARGIN = 5
        val VIEW_BACKGROUND_COLOR = FastColor.ARGB32.color(255, 110, 220, 176)
        val VIEW_OUTLINE_COLOR = FastColor.ARGB32.color(255, 84, 168, 134)
        const val VIEW_WIDTH = 298
        const val VIEW_HEIGHT = 182

        //        val VIEW = cobblenavResource("textures/gui/location/view.png")
        val SORT_ASCENDING = gui("button/sort_button_ascending")
        val SORT_DESCENDING = gui("button/sort_button_descending")
        val REFRESH = gui("button/refresh_button")
    }

    var viewX = 0
    var viewY = 0
    override val color = FastColor.ARGB32.color(255, 63, 126, 101)
    private val spawnDataMap = mutableMapOf<String, List<SpawnData>>()
    lateinit var buckets: List<WeightedBucket>
    var bucketIndex = -1
        set(value) {
            field = max(min(value, buckets.size - 1), 0)
            onBucketChange()
        }
    var currentBucket: WeightedBucket
        get() = buckets[bucketIndex]
        set(value) {
            bucketIndex = buckets.indexOf(value)
        }
    private var sorting = Sorting.ASCENDING
        set(value) {
            field = value
            onSortingChange()
        }
    private lateinit var biome: String
    var loading = false
    private val timer = Timer(LOADING_LOOP_DURATION, true)
    private val frameAmount: Int = ANIMATION_SHEET_WIDTH / FRAME_WIDTH
    override val displayedData: List<SpawnData>
        get() = tableView.items.map { it.child.spawnData }
    override var hoveredData: SpawnData? = null
    override var selectedData: SpawnData? = null
    private lateinit var tableView: TableView<ScrollableItemWidget<SpawnDataWidget>>
    private lateinit var scrollableView: ScrollableView
    private lateinit var bucketSelector: BucketSelectorWidget
    private lateinit var sortButton: IconButton
    private lateinit var refreshButton: IconButton
    private lateinit var checkBox: CheckBox
    private lateinit var supportContextMenu: ContextMenuWidget
    private lateinit var spawnDataDetails: SpawnDataDetailsWidget

    override fun initScreen() {
        viewX = screenX + VERTICAL_BORDER_DEPTH + 5
        viewY = screenY + HORIZONTAL_BORDER_DEPTH + 20

//        RadialPopupMenu(
//            this,
//            screenX + (WIDTH - RadialMenuState.MENU_DIAMETER) / 2,
//            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - RadialMenuState.MENU_DIAMETER / 2
//        ).also { addUnblockableWidget(it) }

        StatusBarWidget(
            screenX + WIDTH - VERTICAL_BORDER_DEPTH - StatusBarWidget.WIDTH - 2,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - StatusBarWidget.HEIGHT
        ).also { addUnblockableWidget(it) }

        sortButton = IconButton(
            pX = viewX + BucketSelectorWidget.WIDTH + BUTTON_BLOCK_SPACE,
            pY = viewY - (BucketSelectorWidget.HEIGHT + BUTTON_HEIGHT) / 2,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            action = {
                this.sorting = if (this.sorting == Sorting.ASCENDING) Sorting.DESCENDING else Sorting.ASCENDING
            },
            texture = SORT_ASCENDING,
            disabled = true
        ).also { addBlockableWidget(it) }

        refreshButton = IconButton(
            pX = viewX + BucketSelectorWidget.WIDTH + BUTTON_BLOCK_SPACE + BUTTON_WIDTH + BUTTON_SPACE,
            pY = viewY - (BucketSelectorWidget.HEIGHT + BUTTON_HEIGHT) / 2,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            disabled = true,
            action = {
                tableView.clear()
                requestSpawnData()
            },
            texture = REFRESH
        ).also { addBlockableWidget(it) }

        RequestLocationScreenInitDataPacket().sendToServer()

//        IconButton(
//            pX = screenX + VERTICAL_BORDER_DEPTH,
//            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
//            pWidth = BACK_BUTTON_SIZE,
//            pHeight = BACK_BUTTON_SIZE,
//            texture = BACK_BUTTON,
//            action = { changeScreen(MainScreen(os)) }
//        ).also { addBlockableWidget(it) }

        tableView = TableView(
            x = viewX + TABLE_MARGIN,
            y = viewY + 1,
            width = VIEW_WIDTH - 2 * TABLE_MARGIN,
            columns = 6,
            verticalGap = 4f,
            horizontalGap = 3f,
            columnWidth = SpawnDataWidget.WIDTH
        )
        scrollableView = ScrollableView(
            viewX + 1,
            tableView.y,
            VIEW_WIDTH - 2,
            VIEW_HEIGHT - 2,
            child = tableView
        ).also { addBlockableWidget(it) }

        checkBox = CheckBox(
            pX = viewX + BUTTON_WIDTH + BUTTON_SPACE/*screenX + BUTTON_WIDTH + VERTICAL_BORDER_DEPTH + BACK_BUTTON_SIZE + 2 * BUTTON_SPACE*/,
            pY = viewY + VIEW_HEIGHT + CHECK_BOX_OFFSET/*screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BUTTON_HEIGHT + CHECK_BOX_OFFSET*/,
            pWidth = CHECK_BOX_WIDTH,
            pHeight = CHECK_BOX_HEIGHT,
            text = Component.translatable("gui.cobblenav.apply_bucket"),
            afterClick = {
                tableView.applyToAll { child ->
                    child.child.spawnData.chanceMultiplier = if ((it as CheckBox).checked) currentBucket.chance else 1f
                }
            },
            default = CobblenavClient.pokenavSettings?.preferences?.applyBucketChecked ?: false
        ).also { addBlockableWidget(it) }

        supportContextMenu = ContextMenuWidget(
            text = listOf(
                Component.translatable("gui.cobblenav.support.location_screen"),
                Component.literal(" "),
                Component.translatable("gui.cobblenav.support.bucket_checkbox")
            ),
            pX = (width - ContextMenuWidget.WIDTH) / 2,
            pY = height / 2,
            lineHeight = 8,
            centerText = false,
            textWidth = ContextMenuWidget.WIDTH - 20,
            cancelAction = { menu, _ ->
                blockWidgets = false
                removeUnblockableWidget(menu)
                menu.openingTimer.reset()
            }
        )

        spawnDataDetails = SpawnDataDetailsWidget(
            displayer = this,
            parentScreen = this,
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH
        ).also { addUnblockableWidget(it) }

        IconButton(
            pX = viewX/*screenX + VERTICAL_BORDER_DEPTH + BACK_BUTTON_SIZE + BUTTON_SPACE*/,
            pY = viewY + VIEW_HEIGHT/*screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BUTTON_HEIGHT*/,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            texture = SUPPORT,
            action = {
                blockWidgets = true
                addUnblockableWidget(supportContextMenu)
            }
        ).also { addBlockableWidget(it) }
    }

    fun receiveInitData(buckets: List<WeightedBucket>, biome: String) {
        this.buckets = buckets
        this.bucketIndex = CobblenavClient.pokenavSettings?.preferences?.bucketIndex ?: 0
        bucketSelector = BucketSelectorWidget(
            viewX, viewY - BucketSelectorWidget.HEIGHT,
            this
        ).also { addBlockableWidget(it) }

        this.biome = biome
        LocationInfoWidget(
            x = viewX + VIEW_WIDTH - LocationInfoWidget.WIDTH,
            y = viewY - LocationInfoWidget.HEIGHT,
            biome = this.biome
        ).also { addBlockableWidget(it) }

        sorting = CobblenavClient.pokenavSettings?.preferences?.sorting ?: Sorting.ASCENDING
        sortButton.disabled = false
    }

    fun receiveSpawnData(spawnDataList: List<SpawnData>) {
        this.spawnDataMap[currentBucket.name] = spawnDataList
        createSpawnDataWidgets(spawnDataList)
        loading = false
        refreshButton.disabled = false
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        guiGraphics.fillWithOutline(
            viewX, viewY,
            viewX + VIEW_WIDTH,
            viewY + VIEW_HEIGHT,
            VIEW_BACKGROUND_COLOR,
            VIEW_OUTLINE_COLOR
        )
//        blitk(
//            matrixStack = poseStack,
//            texture = VIEW,
//            x = viewX,
//            y = viewY,
//            width = VIEW_WIDTH,
//            height = VIEW_WIDTH
//        )
        if (loading) {
            poseStack.pushAndPop(
                translate = Vector3d(0.0, 0.0, 400.0)
            ) {
                renderLoadingAnimation(guiGraphics.pose(), delta)
            }
            return
        }
        if (tableView.isEmpty()) {
            drawScaledText(
                context = guiGraphics,
                text = Component.translatable("gui.cobblenav.empty_spawns_message"),
                x = screenX + WIDTH / 2,
                y = screenY + HEIGHT / 2 - 4,
                maxCharacterWidth = WIDTH - 2 * VERTICAL_BORDER_DEPTH,
                centered = true
            )
        }
    }

    override fun renderOnFrontLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (blockWidgets || minecraft?.screen != this) return
        hoveredData?.let {
            guiGraphics.renderSpawnDataTooltip(
                spawnData = it,
                chanceMultiplier = it.chanceMultiplier,
                mouseX = mouseX,
                mouseY = mouseY,
                x1 = viewX,
                y1 = viewY,
                x2 = viewX + VIEW_WIDTH,
                y2 = viewY + VIEW_HEIGHT,
                delta = delta
            )
        }
        hoveredData = null
    }

    private fun savePreferences() {
        CobblenavClient.pokenavSettings?.let {
            it.preferences = PokenavPreferences(
                bucketIndex = bucketIndex,
                sorting = sorting,
                applyBucketChecked = checkBox.checked
            )
        }
    }

    private fun requestSpawnData() {
        loading = true
        refreshButton.disabled = true
        RequestSpawnMapPacket(currentBucket.name).sendToServer()
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
        tableView.clear()
        val spawnDataList = spawnDataMap[currentBucket.name]
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
        val spawnDataWidgets = spawnDataList
            .sortedWith { firstData, secondData ->
                compareValues(
                    firstData.spawnChance,
                    secondData.spawnChance
                ) * sorting.multiplier
            }
            .map {
                ScrollableItemWidget(
                    child = SpawnDataWidget(
                        x = 0,
                        y = 0,
                        spawnData = it,
                        displayer = this
                    ),
                    topEdge = screenY + HORIZONTAL_BORDER_DEPTH + 16,
                    bottomEdge = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - 15
                )
            }
        tableView.add(spawnDataWidgets)
        checkNearbyPokemon()
    }

    fun checkNearbyPokemon() {
        val nearbyPokemon = player?.let { player ->
            player.clientLevel.getEntitiesOfClass(
                PokemonEntity::class.java,
                AABB.ofSize(
                    player.position(),
                    200.0,
                    200.0,
                    200.0
                )
            ).map { it.pokemon.form.showdownId() }.toHashSet()
        } ?: hashSetOf()
        tableView.applyToAll { item ->
            item.child.isNearby = item.child.spawnData.result.containsResult(nearbyPokemon)
        }
    }

    override fun isBlockingTooltip() = blockWidgets

    override fun selectedCanBeTracked() = selectedData?.result?.canBeTracked() == true
}