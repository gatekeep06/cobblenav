package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.location.BucketSelectorWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.CatalogueEntryWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataDetailWidget
import com.metacontent.cobblenav.networking.packet.server.RequestCatalogueDataPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.CheckedSpawnData
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class CatalogueScreen(
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Map")), SpawnDataDisplayer {
    companion object {
        const val VIEW_WIDTH = 298
        const val VIEW_HEIGHT = 182
        const val TABLE_WIDTH = 282
        const val SCROLLABLE_WIDTH = 296
        const val SCROLLABLE_HEIGHT = 174

        val VIEW = gui("catalogue/view_bg")
    }

    var viewX = 0
    var viewY = 0
    override val color = FastColor.ARGB32.color(255, 58, 150, 182)

    private var spawnData: List<SpawnData>? = null
    override val displayedData: List<SpawnData>?
        get() = spawnData
    override var hoveredData: CheckedSpawnData? = null
    override var selectedData: SpawnData? = null

    private lateinit var scrollableView: ScrollableView
    private lateinit var entryTableView: TableView<ScrollableItemWidget<CatalogueEntryWidget>>
    private lateinit var spawnDataDetails: SpawnDataDetailWidget

    override fun initScreen() {
        viewX = screenX + VERTICAL_BORDER_DEPTH + 5
        viewY = screenY + HORIZONTAL_BORDER_DEPTH + 20

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { changeScreen(MainScreen(os)) }
        ).let { addBlockableWidget(it) }

        entryTableView = TableView(
            x = viewX + 1 + (VIEW_WIDTH - TABLE_WIDTH) / 2,
            y = viewY + 4,
            width = TABLE_WIDTH,
            columns = 3,
            columnWidth = CatalogueEntryWidget.WIDTH,
            verticalGap = 10f,
        )
        scrollableView = ScrollableView(
            x = viewX + 1,
            y = entryTableView.y,
            width = SCROLLABLE_WIDTH,
            height = SCROLLABLE_HEIGHT,
            scissorSpreading = 3,
            child = entryTableView
        ).also { addBlockableWidget(it) }

        val ids = CobblenavClient.spawnDataCatalogue.missingCachedData()
        if (ids.isEmpty()) {
            populateCatalogue()
        } else {
            RequestCatalogueDataPacket(ids).sendToServer()
        }

        spawnDataDetails = SpawnDataDetailWidget(
            displayer = this,
            pokenavScreen = this,
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH
        ).also { addUnblockableWidget(it) }
    }

    fun populateCatalogue() {
        spawnData = CobblenavClient.spawnDataCatalogue.cachedSpawnData.flatMap { it.value }
        val entries = spawnData!!.map { data ->
            ScrollableItemWidget(
                child = CatalogueEntryWidget(data, this),
                topEdge = scrollableView.y,
                bottomEdge = scrollableView.y + scrollableView.height
            )
        }
        entryTableView.add(entries)
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
    }

    override fun isBlockingTooltip(): Boolean = blockWidgets
}