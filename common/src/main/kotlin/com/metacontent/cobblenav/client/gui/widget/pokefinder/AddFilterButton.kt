package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.gui.GuiGraphics

class AddFilterButton(
    val parent: PokefinderScreen,
    val type: RadarFilterType<out RadarFilter>
) : IconButton(
    pWidth = ADD_WIDTH,
    pHeight = ADD_HEIGHT,
    texture = ADD,
    action = { parent.createFilterOfType(type) }
) {
    companion object {
        const val ADD_WIDTH = 46
        const val ADD_HEIGHT = 26
        const val ICON_X = 19
        val ADD = gui("pokefinder/add")
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderWidget(guiGraphics, i, j, f)
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = type.typeIcon,
            x = x + ICON_X,
            y = y,
            width = RadarFilterType.ICON_WIDTH,
            height = RadarFilterType.ICON_HEIGHT
        )
    }
}