package com.metacontent.cobblenav.client.gui.widget.section

import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class CollapsingTextSection(
    statefulWidget: TextSectionWidget,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : WidgetState<TextSectionWidget>(statefulWidget, x, y, width, height, Component.empty()) {
    companion object {
        const val ANIMATION_TIME = 0.1f
    }

    override val blockScreenWidgets = false

    private val timer = Timer(ANIMATION_TIME * statefulWidget.texts.sumOf { it.splittedText.size })

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        timer.tick(f)

        height = TextSectionWidget.HEADER_HEIGHT + (statefulWidget.expandablePartHeight * (1 - timer.getProgress())).toInt()
        statefulWidget.height = height

        statefulWidget.renderBody(guiGraphics, height)
        statefulWidget.renderTitle(guiGraphics, i, j, f)
        statefulWidget.renderFooter(guiGraphics.pose(), x, y + height - TextSectionWidget.FOOTER_HEIGHT)

        if (timer.isOver()) {
            statefulWidget.changeState(CollapsedTextSection(statefulWidget, x, y, width, height))
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return false
    }
}