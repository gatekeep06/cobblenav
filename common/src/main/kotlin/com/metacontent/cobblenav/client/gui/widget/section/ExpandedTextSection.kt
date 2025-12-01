package com.metacontent.cobblenav.client.gui.widget.section

import com.metacontent.cobblenav.client.gui.widget.TextWidget
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class ExpandedTextSection(
    statefulWidget: TextSectionWidget,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : WidgetState<TextSectionWidget>(statefulWidget, x, y, width, height, Component.empty()) {
    override val blockScreenWidgets = false

    private val tableView = TableView<TextWidget>(
        x = x + 4,
        y = y + TextSectionWidget.HEADER_HEIGHT + statefulWidget.paragraphOffset.toInt(),
        width = width - 8,
        columns = 1,
        verticalGap = statefulWidget.paragraphOffset,
        horizontalGap = 0f
    ).also {
        it.add(statefulWidget.texts)
        addWidget(it)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        statefulWidget.renderBody(guiGraphics, height)
        statefulWidget.renderTitle(guiGraphics, i, j, f)
        statefulWidget.renderFooter(guiGraphics.pose(), x, y + height - TextSectionWidget.FOOTER_HEIGHT)
        tableView.render(guiGraphics, i, j, f)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (isValidClickButton(pButton) && clicked(pMouseX, pMouseY)) {
            statefulWidget.changeState(CollapsedTextSection(statefulWidget, x, y, width, TextSectionWidget.HEADER_HEIGHT))
            statefulWidget.height = TextSectionWidget.HEADER_HEIGHT
            return true
        }
        return false
    }

    override fun setX(i: Int) {
        val delta = x - i
        super.setX(i)
        tableView.x -= delta
    }

    override fun setY(i: Int) {
        val delta = y - i
        super.setY(i)
        tableView.y -= delta
    }
}