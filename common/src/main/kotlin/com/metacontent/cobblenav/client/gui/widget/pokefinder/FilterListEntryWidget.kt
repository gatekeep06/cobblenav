package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class FilterListEntryWidget(
    val index: Int,
    val filter: RadarFilter,
    val widget: AbstractWidget,
    val icon: ResourceLocation,
    parent: PokefinderScreen
) : SoundlessWidget(0, 0, WIDTH, HEIGHT, Component.empty()) {
    companion object {
        const val WIDTH = 238
        const val HEIGHT = 26
        const val REMOVE_SIZE = 10
        val ICON_BACKGROUND = gui("pokefinder/icon_background")
        val REMOVE = gui("pokefinder/remove")
    }

    val removeButton = IconButton(
        pX = x,
        pY = y + (HEIGHT - REMOVE_SIZE) / 2,
        pWidth = REMOVE_SIZE,
        pHeight = REMOVE_SIZE,
        action = { parent.removeFilterListEntry(this) },
        texture = REMOVE
    ).also { addWidget(it) }

    init {
        widget.x += REMOVE_SIZE + 2
        addWidget(widget)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        removeButton.render(guiGraphics, i, j, f)

        widget.render(guiGraphics, i, j, f)

        blitk(
            matrixStack = poseStack,
            texture = ICON_BACKGROUND,
            x = widget.x + widget.width,
            y = widget.y,
            width = RadarFilterType.ICON_WIDTH,
            height = RadarFilterType.ICON_HEIGHT
        )
        blitk(
            matrixStack = poseStack,
            texture = icon,
            x = widget.x + widget.width,
            y = widget.y,
            width = RadarFilterType.ICON_WIDTH,
            height = RadarFilterType.ICON_HEIGHT
        )
    }

    override fun setX(i: Int) {
        widget.x += i - x
        removeButton.x += i - x
        super.setX(i)
    }

    override fun setY(i: Int) {
        widget.y += i - y
        removeButton.y += i - y
        super.setY(i)
    }

    override fun setFocused(bl: Boolean) {
        super.setFocused(bl)
        widget.isFocused = bl
    }
}