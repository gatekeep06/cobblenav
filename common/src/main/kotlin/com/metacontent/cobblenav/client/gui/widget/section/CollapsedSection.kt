package com.metacontent.cobblenav.client.gui.widget.section

import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class CollapsedSection(
    statefulWidget: SectionWidget,
    x: Int,
    y: Int,
    width: Int
) : WidgetState<SectionWidget>(statefulWidget, x, y, width, SectionWidget.HEADER_HEIGHT, Component.empty()) {
    override val blockScreenWidgets = false

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        statefulWidget.renderTitle(guiGraphics, i, j, f)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (isValidClickButton(pButton) && clicked(pMouseX, pMouseY)) {
            statefulWidget.changeState(ExpandingSection(statefulWidget, x, y, width, height))
            return true
        }
        return false
    }
}