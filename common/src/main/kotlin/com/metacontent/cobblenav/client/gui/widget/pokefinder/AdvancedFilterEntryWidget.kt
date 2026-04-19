package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.metacontent.cobblenav.client.gui.screen.pokefinder.AdvancedPokefinderScreen
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class AdvancedFilterEntryWidget(
    filter: RadarFilter,
    widget: AbstractWidget,
    icon: ResourceLocation,
    displayName: Component,
    parent: AdvancedPokefinderScreen
) : FilterEntryWidget(filter, widget, icon, displayName) {
    companion object {
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

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        removeButton.render(guiGraphics, i, j, f)
        super.renderWidget(guiGraphics, i, j, f)
    }

    override fun setX(i: Int) {
        removeButton.x += i - x
        super.setX(i)
    }

    override fun setY(i: Int) {
        removeButton.y += i - y
        super.setY(i)
    }
}