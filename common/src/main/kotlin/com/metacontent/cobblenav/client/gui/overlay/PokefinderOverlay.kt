package com.metacontent.cobblenav.client.gui.overlay

import com.metacontent.cobblenav.item.Pokefinder
import com.metacontent.cobblenav.util.log
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.FastColor

class PokefinderOverlay : Gui(Minecraft.getInstance()) {
    companion object {
        const val OFFSET: Int = 10
        const val WIDTH: Int = 144
        const val HEIGHT: Int = 96
    }

    private val minecraft = Minecraft.getInstance()

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val isRightHand = minecraft.player?.mainHandItem?.item is Pokefinder
        val scale = minecraft.window.guiScaledWidth.toDouble() / minecraft.window.screenWidth.toDouble() * minecraft.window.guiScale
        log(minecraft.window.width.toString() + " " + minecraft.window.screenWidth + " " + minecraft.window.guiScaledWidth)
        val width = (WIDTH / scale).toInt()
        val height = (HEIGHT / scale).toInt()
        val offset = (OFFSET / scale).toInt()
        val x = if (isRightHand) minecraft.window.guiScaledWidth - width - offset else offset
        val y = minecraft.window.guiScaledHeight - height - offset

        guiGraphics.fill(x, y, x + width, y + height, FastColor.ARGB32.color(255, 255, 255, 255))
    }
}