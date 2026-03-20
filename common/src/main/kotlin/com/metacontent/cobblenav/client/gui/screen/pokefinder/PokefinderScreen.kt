package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.pokefinder.FilterListEntryWidget
import com.metacontent.cobblenav.client.settings.pokefinder.RadarFilterTypeRegistry
import com.metacontent.cobblenav.client.settings.pokefinder.type.createEntry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class PokefinderScreen : Screen(Component.literal("Pokefinder")) {
    companion object {
        const val WIDTH = 288
        const val HEIGHT = 192
        const val BORDER_WIDTH = 5
        const val BUTTON_SIZE = 18
        val BACKGROUND = gui("pokefinder/background")
        val ADD = gui("pokefinder/add")
        val BACK = gui("pokefinder/back")
        val CLEAR = gui("pokefinder/clear")

        @JvmStatic
        val COLOR = FastColor.ARGB32.color(255, 1, 235, 95)
    }

    val player: LocalPlayer? = Minecraft.getInstance().player

    init {
        player?.playSound(CobblemonSounds.PC_ON, 0.1f, 2f)
    }

    private var screenX = 0
    private var screenY = 0

    private lateinit var filterTable: TableView<AbstractWidget>
    private lateinit var baseTable: TableView<AbstractWidget>
    private lateinit var scrollableView: ScrollableView
    private lateinit var backButton: IconButton
    private lateinit var clearButton: IconButton

    private val settings = CobblenavClient.pokefinderSettings

    override fun init() {
        screenX = (width - WIDTH) / 2
        screenY = (height - HEIGHT) / 2

        filterTable = TableView<AbstractWidget>(
            x = screenX + BORDER_WIDTH + 36,
            y = screenY + BORDER_WIDTH + 16,
            width = 238,
            columns = 1,
            verticalGap = 6f,
            horizontalGap = 0f
        ).also { table ->
            settings?.getFilters()?.mapNotNull {
                RadarFilterTypeRegistry.get(it.type)?.createEntry(this, it)
            }?.let { table.add(it) }
        }

        baseTable = TableView<AbstractWidget>(
            x = screenX + BORDER_WIDTH + 36,
            y = screenY + BORDER_WIDTH + 16,
            width = 238,
            columns = 1,
            verticalGap = 0f,
            horizontalGap = 0f
        ).also {
            it.add(filterTable)
            it.add(
                IconButton(
                    pWidth = 218,
                    pHeight = 26,
                    texture = ADD,
                    action = {
                        val type = RadarFilterTypeRegistry.get("properties") ?: return@IconButton
                        val entry = type.createEntry(this)
                        settings?.addFilter(entry.filter)
                        filterTable.add(entry)
                    }
                )
            )
        }

        scrollableView = ScrollableView(
            x = filterTable.x,
            y = filterTable.y,
            width = 241,
            height = 150,
            child = baseTable
        ).also { addRenderableWidget(it) }

        backButton = IconButton(
            pX = screenX + BORDER_WIDTH + 1,
            pY = screenY + HEIGHT - BORDER_WIDTH - 1 - BUTTON_SIZE,
            pWidth = BUTTON_SIZE,
            pHeight = BUTTON_SIZE,
            action = { onClose() },
            texture = BACK
        ).also { addRenderableWidget(it) }
        clearButton = IconButton(
            pX = screenX + BORDER_WIDTH + 1,
            pY = screenY + BORDER_WIDTH + 1,
            pWidth = BUTTON_SIZE,
            pHeight = BUTTON_SIZE,
            action = {
                settings?.clearFilters()
                filterTable.clear()
            },
            texture = CLEAR
        ).also { addRenderableWidget(it) }
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderBackground(guiGraphics, i, j, f)
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = BACKGROUND,
            x = screenX,
            y = screenY,
            width = WIDTH,
            height = HEIGHT
        )
    }

    fun removeFilterListEntry(entry: FilterListEntryWidget) {
        settings?.removeFilter(entry.filter)
        filterTable.remove(entry)
    }

    override fun isPauseScreen(): Boolean = false
}