package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

open class FilterEntryWidget(
    val filter: RadarFilter,
    val widget: AbstractWidget,
    val icon: ResourceLocation,
    displayName: Component
) : SoundlessWidget(0, 0, WIDTH, HEIGHT, displayName) {
    companion object {
        const val WIDTH = 238
        const val HEIGHT = 26
        const val REMOVE_SIZE = 16
        val ICON_BACKGROUND = gui("pokefinder/icon_background")
    }

    init {
        widget.x += REMOVE_SIZE + 4
        addWidget(widget)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

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
        super.setX(i)
    }

    override fun setY(i: Int) {
        widget.y += i - y
        super.setY(i)
    }

    override fun setFocused(bl: Boolean) {
        super.setFocused(bl)
        widget.isFocused = bl
    }
}