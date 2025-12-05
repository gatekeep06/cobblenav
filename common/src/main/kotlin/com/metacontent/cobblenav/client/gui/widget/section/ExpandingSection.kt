package com.metacontent.cobblenav.client.gui.widget.section

import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.widget.section.SectionWidget.Companion.FOOTER_HEIGHT
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class ExpandingSection(
    statefulWidget: SectionWidget,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : WidgetState<SectionWidget>(statefulWidget, x, y, width, height, Component.empty()) {
    companion object {
        const val ANIMATION_TIME = 0.25f
    }

    override val blockScreenWidgets = false

    private val timer = Timer(ANIMATION_TIME * statefulWidget.widgets.sumOf { it.height / 12 })

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        timer.tick(f)

        height = SectionWidget.HEADER_HEIGHT + FOOTER_HEIGHT + (statefulWidget.expandablePartHeight * timer.getProgress()).toInt()
        statefulWidget.height = height

        statefulWidget.renderBody(guiGraphics, height)
        statefulWidget.renderTitle(guiGraphics, i, j, f)
        statefulWidget.renderFooter(guiGraphics.pose(), x, y + height - FOOTER_HEIGHT)

        if (timer.isOver()) {
            statefulWidget.changeState(ExpandedSection(statefulWidget, x, y, width))
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return false
    }
}