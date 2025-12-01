package com.metacontent.cobblenav.client.gui.widget.section

import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class CollapsedTextSection(
    statefulWidget: TextSectionWidget,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : WidgetState<TextSectionWidget>(statefulWidget, x, y, width, height, Component.empty()) {
    override val blockScreenWidgets = false

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        statefulWidget.renderTitle(guiGraphics, i, j, f)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (isValidClickButton(pButton) && clicked(pMouseX, pMouseY)) {
            statefulWidget.changeState(ExpandingTextSection(statefulWidget, x, y, width, height))
            return true
        }
        return false
    }
}