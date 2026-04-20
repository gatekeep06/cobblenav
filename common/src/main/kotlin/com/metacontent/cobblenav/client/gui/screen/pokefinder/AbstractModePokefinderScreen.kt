package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.overlay.PokefinderOverlay.Companion.RADIUS
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.pokefinder.FilterEntryWidget
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.phys.AABB

abstract class AbstractModePokefinderScreen : PokefinderScreen() {
    companion object {
        const val CHANGE_MODE_SIZE = 10
        const val CHANGE_MODE_X = 270
        const val CHANGE_MODE_Y = 8
        val DETAILS = gui("pokefinder/details")
        val CHANGE_MODE = gui("pokefinder/change_mode")
    }

    protected val settings = CobblenavClient.pokefinderSettings
    private var bottomText: Component? = null

    private lateinit var baseTable: TableView<AbstractWidget>
    private lateinit var scrollableView: ScrollableView
    private lateinit var clearButton: IconButton
    private lateinit var changeModeButton: IconButton

    override fun initScreen() {
        super.initScreen()

        baseTable = TableView(
            x = screenX + BORDER_WIDTH + 36,
            y = screenY + BORDER_WIDTH + 16,
            width = 238,
            columns = 1,
            verticalGap = 0f,
            horizontalGap = 0f
        )
        populateBaseTable(baseTable::add)

        scrollableView = ScrollableView(
            x = baseTable.x,
            y = baseTable.y,
            width = 241,
            height = 150,
            child = baseTable
        ).also { addRenderableWidget(it) }

        clearButton = IconButton(
            pX = screenX + BORDER_WIDTH + 1,
            pY = screenY + BORDER_WIDTH + 1,
            pWidth = BUTTON_SIZE,
            pHeight = BUTTON_SIZE,
            action = { clearFilters() },
            texture = CLEAR
        ).also { addRenderableWidget(it) }

        changeModeButton = IconButton(
            pX = screenX + CHANGE_MODE_X,
            pY = screenY + CHANGE_MODE_Y,
            pWidth = CHANGE_MODE_SIZE,
            pHeight = CHANGE_MODE_SIZE,
            texture = CHANGE_MODE,
            action = {
                settings?.mode = null
                Minecraft.getInstance().setScreen(ModeSelectionPokefinderScreen())
            }
        ).also { addRenderableWidget(it) }
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderBackground(guiGraphics, i, j, f)

        blitk(
            matrixStack = guiGraphics.pose(),
            texture = DETAILS,
            x = screenX,
            y = screenY,
            width = WIDTH,
            height = HEIGHT
        )

        val pos = player?.position()
        val entityNumber = if (pos != null && settings != null) {
            minecraft?.level?.getEntitiesOfClass(
                PokemonEntity::class.java,
                AABB.ofSize(pos, RADIUS, RADIUS, RADIUS)
            ) { settings.test(it.pokemon) }?.size ?: 0
        } else {
            0
        }
        drawScaledText(
            context = guiGraphics,
            text = Component.literal(entityNumber.toString().padStart(3, '0')),
            x = screenX + BORDER_WIDTH + 14,
            y = screenY + 86 + 7,
            maxCharacterWidth = 17,
            colour = color,
            centered = true
        )

        bottomText = checkBottomText()
        bottomText?.let {
            drawScaledText(
                context = guiGraphics,
                text = it as MutableComponent,
                x = screenX + BORDER_WIDTH + 87,
                y = screenY + BORDER_WIDTH + 171,
                maxCharacterWidth = 186,
                colour = color
            )
        }
    }

    abstract fun populateBaseTable(consumer: (AbstractWidget) -> Unit)

    abstract fun clearFilters()

    protected open fun checkBottomText(): Component? {
        return if (clearButton.isHovered) {
            translate("gui.cobblenav.pokefinder.clear")
        } else if (changeModeButton.isHovered) {
            translate("gui.cobblenav.pokefinder.change_mode")
        } else if (backButton.isHovered) {
            translate("gui.cobblenav.pokefinder.back")
        } else {
            null
        }
    }

    abstract fun <T : RadarFilter> createEntry(
        type: RadarFilterType<T>,
        filter: T,
    ): FilterEntryWidget
}