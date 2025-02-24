package com.metacontent.cobblenav.client.gui.widget.layout.scrollable

import com.metacontent.cobblenav.client.gui.widget.ContainerWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget

class ScrollableItemWidget<T : AbstractWidget>(
    child: T,
    val topEdge: Int,
    val bottomEdge: Int
) : ContainerWidget<T>(child) {
    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (y > bottomEdge || y + height < topEdge) return
        child.isFocused = j in (topEdge + 4)..<(bottomEdge - 2)
        child.render(guiGraphics, i, j, f)
    }
}