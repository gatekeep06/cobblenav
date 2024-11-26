package com.metacontent.cobblenav.client.gui.widget.layout.scrollable

import com.metacontent.cobblenav.Cobblenav
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import kotlin.math.abs

class ScrollThumbWidget(
    x: Int, y: Int,
    val parent: ScrollableView
) : AbstractWidget(x, y, WIDTH, 0, Component.literal(" Scroll Thumb")) {
    companion object {
        const val WIDTH: Int = 2
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (parent.child.height < parent.height) return
        height = parent.height * parent.height / parent.child.height
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

    override fun onDrag(d: Double, e: Double, f: Double, g: Double) {
        if (parent.child.height < parent.height) return
        // TODO: improve scrolling
        parent.scrolled = ((e - height / 2.0 - parent.y) * (parent.child.height - parent.height) / (parent.height - height)).toInt()
    }

    override fun playDownSound(soundManager: SoundManager) {}

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
}