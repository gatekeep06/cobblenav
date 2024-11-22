package com.metacontent.cobblenav.client.gui.widget.layout.scrollable

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class ScrollThumbWidget(
    x: Int, y: Int,
    val parent: ScrollableView,
    private val drag: (ScrollableView, Double, Double, Int) -> Unit,
    private val setter: (ScrollThumbWidget) -> Unit,
    private val getter: (ScrollThumbWidget) -> Int
) : AbstractWidget(x, y, SIZE, SIZE, Component.literal("Scroll Thumb")) {
    companion object {
        const val SIZE: Int = 2
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (parent.difference < 0) return
        setter.invoke(this)
        guiGraphics.fill(
            x, y,
            x + width,
            y + height,
            FastColor.ARGB32.color(100 + if (isHovered()) 30 else 0, 255, 255, 255)
        )
    }

    override fun onClick(d: Double, e: Double) {
        isFocused = true
    }

    override fun onRelease(d: Double, e: Double) {
        isFocused = false
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        if (isValidClickButton(i) && isFocused && g != 0.0) {
            onDrag(d, e, f, g)
            return true
        }
        return false
    }

    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double) {
        if (parent.difference < 0) return
        drag.invoke(parent, mouseX, mouseY, getter.invoke(this))
    }

    override fun playDownSound(soundManager: SoundManager) {}

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
}