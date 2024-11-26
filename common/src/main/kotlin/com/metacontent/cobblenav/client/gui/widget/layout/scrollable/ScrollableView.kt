package com.metacontent.cobblenav.client.gui.widget.layout.scrollable

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import kotlin.math.max
import kotlin.math.min

class ScrollableView(
    x: Int, y: Int,
    width: Int, height: Int,
    private val scrollMultiplier: Float = 20f,
    val child: AbstractWidget,
    private val setter: (AbstractWidget, Int) -> Unit = { widget, value -> widget.y = value },
    private val subtraction: (ScrollableView, AbstractWidget) -> Int = { view, widget -> widget.height - view.height },
    drag: (ScrollableView, Double, Double, Int) -> Unit = { view, _, mouseY, thumbSize ->
        view.scrolled = ((mouseY - thumbSize / 2.0 - view.y) * view.difference / (view.height - thumbSize)).toInt()
    },
    thumbSizeSetter: (ScrollThumbWidget) -> Unit = { it.height = it.parent.height * it.parent.height / it.parent.child.height },
    thumbSizeGetter: (ScrollThumbWidget) -> Int = { it.height },
    thumbX: Int = x + width - ScrollThumbWidget.SIZE,
    thumbY: Int = y,
) : SoundlessWidget(x, y, width, height, Component.literal("Scrollable View")) {
    val difference: Int
        get() = subtraction.invoke(this, child)
    var scrolled = 0
        set(value) {
            field = max(min(value, difference), 0)
            onScroll()
        }

    init {
        addWidget(child)
    }

    private val scrollThumb = ScrollThumbWidget(
        x = thumbX,
        y = thumbY,
        parent = this,
        drag = drag,
        setter = thumbSizeSetter,
        getter = thumbSizeGetter
    ).also { addWidget(it) }

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
        if (difference > 0) {
            scrolled -= (verticalAmount * scrollMultiplier).toInt()
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    private fun onScroll() {
        setter.invoke(child, y - scrolled)
        setter.invoke(scrollThumb, (y + (height - scrollThumb.height) * (scrolled.toDouble() / difference.toDouble())).toInt())
    }

    fun reset() {
        scrolled = 0
    }
}