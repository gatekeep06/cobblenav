package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.Sorting
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.ContextMenuWidget
import com.metacontent.cobblenav.client.gui.widget.StatusBarWidget
import com.metacontent.cobblenav.client.gui.widget.button.CheckBox
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.location.BucketSelectorWidget
import com.metacontent.cobblenav.client.gui.widget.location.LocationInfoWidget
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialMenuState
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialPopupMenu
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataDetailWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataWidget
import com.metacontent.cobblenav.client.settings.PokenavPreferences
import com.metacontent.cobblenav.networking.packet.server.RequestLocationScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestSpawnMapPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.CheckedSpawnData
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import org.joml.Vector3d
import kotlin.math.max
import kotlin.math.min

class LocationScreen(
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false,
    val fixedAreaPoint: BlockPos? = null
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
        const val CHECK_BOX_SIZE = 8
        const val CHECK_BOX_OFFSET = 4
        const val TABLE_MARGIN = 5
        const val VIEW_WIDTH = 298
        const val VIEW_HEIGHT = 182

        val VIEW = gui("location/view_bg")
        val SORT_ASCENDING = gui("button/sort_button_ascending")
        val SORT_DESCENDING = gui("button/sort_button_descending")
        val REFRESH = gui("button/refresh_button")
    }

    var viewX = 0
    var viewY = 0
    override val color = FastColor.ARGB32.color(255, 63, 126, 101)
    private val spawnDataMap = mutableMapOf<String, List<CheckedSpawnData>>()
    private val weightedBuckets = mutableMapOf<String, WeightedBucket>()
    lateinit var buckets: List<String>
    var bucketIndex = -1
        set(value) {
            field = max(min(value, buckets.size - 1), 0)
            onBucketChange()
        }
    var currentBucket: String
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
        get() = tableView.items.map { it.child.spawnData.data }
    override var hoveredData: CheckedSpawnData? = null
    override var selectedData: SpawnData? = null
    private lateinit var tableView: TableView<ScrollableItemWidget<SpawnDataWidget>>
    private lateinit var scrollableView: ScrollableView
    private lateinit var bucketSelector: BucketSelectorWidget
    private lateinit var sortButton: IconButton
    private lateinit var refreshButton: IconButton
    private lateinit var checkBox: CheckBox
    private lateinit var supportContextMenu: ContextMenuWidget
    private lateinit var spawnDataDetails: SpawnDataDetailWidget

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
            x = viewX + BUTTON_WIDTH + BUTTON_SPACE/*screenX + BUTTON_WIDTH + VERTICAL_BORDER_DEPTH + BACK_BUTTON_SIZE + 2 * BUTTON_SPACE*/,
            y = viewY + VIEW_HEIGHT + CHECK_BOX_OFFSET/*screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BUTTON_HEIGHT + CHECK_BOX_OFFSET*/,
            height = CHECK_BOX_SIZE,
            width = CHECK_BOX_SIZE,
            text = Component.translatable("gui.cobblenav.apply_bucket"),
            texture = CheckBox.CHECK_BOX,
            default = CobblenavClient.pokenavSettings?.preferences?.applyBucketChecked ?: false
        ) {
            tableView.applyToAll { child ->
                child.child.spawnData.chanceMultiplier =
                    if (it.checked()) weightedBuckets[currentBucket]?.chance ?: 1f else 1f
            }
        }.also { addBlockableWidget(it) }

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

        spawnDataDetails = SpawnDataDetailWidget(
            displayer = this,
            pokenavScreen = this,
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

    fun receiveInitData(buckets: List<String>, biome: String) {
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

        val newlyCatalogued = CobblenavClient.spawnDataCatalogue.newlyCataloguedAmount
        if (newlyCatalogued > 0) {
            notifications.add(Component.translatable("gui.cobblenav.notification.newly_catalogued", newlyCatalogued))
            CobblenavClient.spawnDataCatalogue.newlyCataloguedAmount = 0
        }
        if (fixedAreaPoint != null) {
            notifications.add(Component.translatable("gui.cobblenav.notification.pokesnack"))
        }
    }

    fun receiveSpawnData(spawnDataList: List<CheckedSpawnData>, weightedBucket: WeightedBucket) {
        spawnDataMap[currentBucket] = spawnDataList
        weightedBuckets[currentBucket] = weightedBucket
        createSpawnDataWidgets(spawnDataList)
        loading = false
        refreshButton.disabled = false
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        blitk(
            matrixStack = poseStack,
            texture = VIEW,
            x = viewX,
            y = viewY - BucketSelectorWidget.HEIGHT,
            width = VIEW_WIDTH,
            height = VIEW_HEIGHT + BucketSelectorWidget.HEIGHT
        )
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
        hoveredData?.renderTooltip(
            guiGraphics = guiGraphics,
            mouseX = mouseX,
            mouseY = mouseY,
            x1 = viewX,
            y1 = viewY,
            x2 = viewX + VIEW_WIDTH,
            y2 = viewY + VIEW_HEIGHT,
            delta = delta
        )
        hoveredData = null
    }

    private fun savePreferences() {
        CobblenavClient.pokenavSettings?.let {
            it.preferences = PokenavPreferences(
                bucketIndex = bucketIndex,
                sorting = sorting,
                applyBucketChecked = checkBox.checked()
            )
        }
    }

    private fun requestSpawnData() {
        loading = true
        refreshButton.disabled = true
        RequestSpawnMapPacket(currentBucket, fixedAreaPoint).sendToServer()
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
        val spawnDataList = spawnDataMap[currentBucket]
        if (spawnDataList == null) {
            requestSpawnData()
            return
        }
        createSpawnDataWidgets(spawnDataList)
    }

    private fun onSortingChange() {
        sortButton.texture = if (sorting == Sorting.ASCENDING) SORT_ASCENDING else SORT_DESCENDING
        tableView.resort(sorting) { widget -> widget.child.spawnData.chance }
    }

    private fun createSpawnDataWidgets(spawnDataList: List<CheckedSpawnData>) {
        val spawnDataWidgets = spawnDataList
            .sortedWith { firstData, secondData ->
                compareValues(
                    firstData.chance,
                    secondData.chance
                ) * sorting.multiplier
            }
            .map {
                ScrollableItemWidget(
                    child = SpawnDataWidget(
                        x = 0,
                        y = 0,
                        spawnData = it.also { data ->
                            data.chanceMultiplier =
                                if (checkBox.checked()) weightedBuckets[currentBucket]?.chance ?: 1f else 1f
                        },
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
                    128.0,
                    128.0,
                    128.0
                )
            ).groupBy { it.pokemon.form.showdownId() }.mapValues { it.value.map(Entity::getId) }
        } ?: emptyMap()
        tableView.applyToAll { item ->
            item.child.nearbyEntityIds = nearbyPokemon[item.child.spawnData.data.result.getResultId()] ?: emptyList()
        }
    }

    override fun isBlockingTooltip() = blockWidgets

    override fun selectedCanBeTracked() = selectedData?.result?.canBeTracked() == true
}