package com.metacontent.cobblenav.client.gui.widget.section

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.stateful.StatefulWidget
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class SectionWidget(
    x: Int,
    y: Int,
    width: Int,
    val title: MutableComponent,
    val texts: List<AbstractWidget>,
    val color: RGB = RGB(178, 228, 188),
//    val headerColor: RGB = RGB(199, 239, 207),
    val paragraphOffset: Float = 3f
) : StatefulWidget(null, x, y, width, HEADER_HEIGHT, Component.literal("Text Section")) {
    companion object {
        const val HEADER_HEIGHT = 20
        const val FOOTER_HEIGHT = 2
        const val HEADER_SIDE_WIDTH = 6
        val HEADER_1 = gui("text_section_header_1")
        val HEADER_2 = gui("text_section_header_2")
        val HEADER_3 = gui("text_section_header_3")
        val FOOTER = gui("text_section_footer")
    }

    val expandablePartHeight = (paragraphOffset * texts.size).toInt() + texts.sumOf { it.height }

    init {
        height += expandablePartHeight
    }

    override var state = initState(ExpandedSection(this, x, y, width, height))

    fun renderTitle(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = HEADER_1,
            x = x,
            y = y,
            width = HEADER_SIDE_WIDTH,
            height = HEADER_HEIGHT,
            red = color.red(),
            green = color.green(),
            blue = color.blue()
        )
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = HEADER_2,
            x = x + HEADER_SIDE_WIDTH,
            y = y,
            width = width - 2 * HEADER_SIDE_WIDTH,
            height = HEADER_HEIGHT,
            red = color.red(),
            green = color.green(),
            blue = color.blue()
        )
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = HEADER_3,
            x = x + width - HEADER_SIDE_WIDTH,
            y = y,
            width = HEADER_SIDE_WIDTH,
            height = HEADER_HEIGHT,
            red = color.red(),
            green = color.green(),
            blue = color.blue()
        )
        drawScaledText(
            context = guiGraphics,
            text = title,
            x = x + 4,
            y = y + 1 + (HEADER_HEIGHT - Minecraft.getInstance().font.lineHeight) / 2,
            maxCharacterWidth = width - 8
        )
    }

    fun renderBody(guiGraphics: GuiGraphics, height: Int = this.height - HEADER_HEIGHT) {
        guiGraphics.fill(x, y + HEADER_HEIGHT / 2, x + width, y + height - FOOTER_HEIGHT, color.toColor())
    }

    fun renderFooter(poseStack: PoseStack, x: Int, y: Int) {
        blitk(
            matrixStack = poseStack,
            texture = FOOTER,
            x = x,
            y = y,
            width = width,
            height = FOOTER_HEIGHT,
            red = color.red(),
            green = color.green(),
            blue = color.blue()
        )
    }
}