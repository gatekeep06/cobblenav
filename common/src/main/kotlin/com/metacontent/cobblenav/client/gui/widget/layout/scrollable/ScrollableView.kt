package com.metacontent.cobblenav.client.gui.widget.layout.scrollable

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ScrollableView(
    x: Int, y: Int,
    width: Int, height: Int,
    private val scrollMultiplier: Float = 20f,
    val child: AbstractWidget
) : SoundlessWidget(x, y, width, height, Component.literal("Scrollable View")) {
    var scrolled = 0
        set(value) {
            field = max(min(value, child.height - height), 0)
            onScroll()
        }

    init {
        addWidget(child)
    }

    private val scrollThumb = ScrollThumbWidget(x + width - ScrollThumbWidget.WIDTH, y, this).also { addWidget(it) }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.enableScissor(x, y, x + width, y + height)
        child.render(guiGraphics, i, j, f)
        scrollThumb.render(guiGraphics, i, j, f)
        guiGraphics.disableScissor()
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (isHovered()) {
            return super.mouseClicked(pMouseX, pMouseY, pButton)
        }
        return false
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        if (child.height > height) {
            scrolled -= (verticalAmount * scrollMultiplier).toInt()
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    private fun onScroll() {
        child.y = y - scrolled
        scrollThumb.y = (y + (height - scrollThumb.height) * (scrolled.toDouble() / (child.height - height).toDouble())).toInt()
    }

    fun reset() {
        scrolled = 0
    }
}