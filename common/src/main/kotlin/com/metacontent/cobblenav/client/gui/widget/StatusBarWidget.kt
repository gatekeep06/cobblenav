package com.metacontent.cobblenav.client.gui.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class StatusBarWidget(
    x: Int, y: Int
) : SoundlessWidget(x, y, WIDTH, HEIGHT, Component.literal("Status Bar")) {
    companion object {
        const val DECORATION_WIDTH: Int = 16
        const val CLOCKS_WIDTH: Int = 30
        const val SPACE: Int = 4
        const val WIDTH: Int = DECORATION_WIDTH + CLOCKS_WIDTH + SPACE
        const val HEIGHT: Int = 6
        const val TEXT_SCALE: Float = 0.7f
        val TEXTURE = cobblenavResource("textures/gui/status_bar.png")
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        val dayTime = Minecraft.getInstance().level?.dayTime ?: 0L

        drawScaledText(
            context = guiGraphics,
            text = Component.literal(getTimeString(dayTime)),
            x = x + DECORATION_WIDTH + SPACE,
            y = y + 1,
            scale = TEXT_SCALE,
            maxCharacterWidth = (CLOCKS_WIDTH / TEXT_SCALE).toInt()
        )

        blitk(
            matrixStack = poseStack,
            texture = TEXTURE,
            x = x,
            y = y,
            width = DECORATION_WIDTH,
            height = height
        )
    }

    private fun getTimeString(dayTime: Long): String {
        val adjustedTime = (dayTime + 6000) % 24000
        var hours = (adjustedTime / 1000).toInt()
        val minutes = ((adjustedTime % 1000) * 60 / 1000).toInt()
        val period = if (hours >= 12) "PM" else "AM"
        hours %= 12
        hours = if (hours == 0) 12 else hours

        return String.format("%02d:%02d %s", hours, minutes, period)
    }
}