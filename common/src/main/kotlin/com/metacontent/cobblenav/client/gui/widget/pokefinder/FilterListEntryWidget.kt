package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.api.pokefinder.RadarDotTypeRepository
import com.metacontent.cobblenav.client.gui.overlay.PokefinderOverlay
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class FilterListEntryWidget(
    var index: Int,
    val filter: RadarFilter,
    val widget: AbstractWidget,
    val icon: ResourceLocation,
    val parent: PokefinderScreen
) : SoundlessWidget(0, 0, WIDTH, HEIGHT, Component.empty()) {
    companion object {
        const val WIDTH = 238
        const val HEIGHT = 26
        const val BUTTON_SIZE = 10
        const val BUTTON_OFFSET = 2
        const val ICON_WIDTH = 22
        const val INFO_WIDTH = 17
        const val INDEX_X = 9
        const val INDEX_Y = 5
        const val DOT_X = 8
        const val DOT_Y = 19
        const val INDEX_WIDTH = 13
        val ICON_BACKGROUND = gui("pokefinder/icon_background")
        val INFO = gui("pokefinder/filter_info")
        val REMOVE = gui("pokefinder/remove")
        val CHANGE = gui("pokefinder/change_dot")
    }

    val removeButton = IconButton(
        pX = x,
        pY = y + (HEIGHT - 2 * BUTTON_SIZE - BUTTON_OFFSET) / 2,
        pWidth = BUTTON_SIZE,
        pHeight = BUTTON_SIZE,
        action = { parent.removeFilterListEntry(this) },
        texture = REMOVE
    ).also { addWidget(it) }
    val changeButton = IconButton(
        pX = removeButton.x,
        pY = removeButton.y + BUTTON_SIZE + BUTTON_OFFSET,
        pWidth = BUTTON_SIZE,
        pHeight = BUTTON_SIZE,
        action = { filter.dotType = RadarDotTypeRepository.next(filter.dotType?.id) },
        texture = CHANGE
    ).also { addWidget(it) }

    init {
        widget.x += BUTTON_SIZE + 3 + INFO_WIDTH
        addWidget(widget)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        removeButton.render(guiGraphics, i, j, f)
        changeButton.render(guiGraphics, i, j, f)

        blitk(
            matrixStack = poseStack,
            texture = INFO,
            x = widget.x - INFO_WIDTH,
            y = widget.y,
            width = INFO_WIDTH,
            height = widget.height
        )
        drawScaledText(
            context = guiGraphics,
            text = literal(index + 1),
            x = widget.x - INFO_WIDTH + INDEX_X,
            y = widget.y + INDEX_Y,
            centered = true,
            colour = PokefinderScreen.COLOR,
            maxCharacterWidth = INDEX_WIDTH
        )
        blitk(
            matrixStack = poseStack,
            texture = filter.dotType?.texture(),
            x = widget.x - INFO_WIDTH + DOT_X,
            y = widget.y + DOT_Y,
            width = PokefinderOverlay.DOT_SIZE,
            height = PokefinderOverlay.DOT_SIZE
        )

        widget.render(guiGraphics, i, j, f)

        blitk(
            matrixStack = poseStack,
            texture = ICON_BACKGROUND,
            x = widget.x + widget.width,
            y = widget.y,
            width = ICON_WIDTH,
            height = widget.height
        )
        blitk(
            matrixStack = poseStack,
            texture = icon,
            x = widget.x + widget.width,
            y = widget.y,
            width = ICON_WIDTH,
            height = widget.height
        )

        if (removeButton.isHovered) {
            parent.bottomText = translate("gui.cobblenav.pokefinder.remove")
        } else if (changeButton.isHovered) {
            parent.bottomText = translate("gui.cobblenav.pokefinder.change")
        }
    }

    override fun setX(i: Int) {
        widget.x += i - x
        removeButton.x += i - x
        changeButton.x += i - x
        super.setX(i)
    }

    override fun setY(i: Int) {
        widget.y += i - y
        removeButton.y += i - y
        changeButton.y += i - y
        super.setY(i)
    }

    override fun setFocused(bl: Boolean) {
        super.setFocused(bl)
        widget.isFocused = bl
    }
}