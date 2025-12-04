package com.metacontent.cobblenav.client.gui.widget

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.util.splitText
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

open class TextWidget(
    x: Int,
    y: Int,
    width: Int,
    text: MutableComponent,
    val lineHeight: Int = Minecraft.getInstance().font.lineHeight,
    val lineOffset: Int = 1,
    val centered: Boolean = false,
    val shadow: Boolean = false
) : SoundlessWidget(x, y, width, 0, Component.empty()) {
    val splittedText = splitText(text, width)

    init {
        height = splittedText.size * lineHeight + (splittedText.size - 1) * lineOffset
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        var lineY = y
        splittedText.forEach {
            drawScaledText(
                context = guiGraphics,
                text = it,
                x = x,
                y = lineY,
                maxCharacterWidth = width,
                centered = centered,
                shadow = shadow
            )
            lineY += lineHeight + lineOffset
        }
    }
}