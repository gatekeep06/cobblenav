package com.metacontent.cobblenav.client.gui.widget.section

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.stateful.StatefulWidget
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
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
    val widgets: List<AbstractWidget>,
    val color: RGB = RGB(178, 228, 188),
//    val headerColor: RGB = RGB(199, 239, 207),
    val paragraphOffset: Float = 3f
) : StatefulWidget(null, x, y, width, HEADER_HEIGHT, Component.literal("Text Section")) {
    companion object {
        const val HEADER_HEIGHT = 20
        const val FOOTER_HEIGHT = 2
        const val HEADER_SIDE_WIDTH = 6
        const val ALPHA = 1f
        val HEADER_1 = gui("text_section_header_1")
        val HEADER_2 = gui("text_section_header_2")
        val HEADER_3 = gui("text_section_header_3")
        val FOOTER = gui("text_section_footer")
    }

    val expandablePartHeight = widgets.sumOf { if (it.height != 0) it.height + paragraphOffset.toInt() else 0 }

    override var state = initState(ExpandedSection(this, x, y, width))

    override fun initState(state: WidgetState<*>): WidgetState<*> {
        height = state.height
        return super.initState(state)
    }

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
            blue = color.blue(),
            alpha = ALPHA
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
            blue = color.blue(),
            alpha = ALPHA
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
            blue = color.blue(),
            alpha = ALPHA
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
        guiGraphics.fill(x, y + HEADER_HEIGHT / 2, x + width, y + height - FOOTER_HEIGHT, color.toColor((ALPHA * RGB.MAX_VALUE).toInt()))
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
            blue = color.blue(),
            alpha = ALPHA
        )
    }
}