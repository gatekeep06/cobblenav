package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.client.render.drawScaledText
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.FastColor
import net.minecraft.world.item.ItemStack
import kotlin.math.max
import kotlin.math.min

fun GuiGraphics.renderMultilineTextTooltip(
    header: MutableComponent,
    body: MutableComponent,
    targetWidth: Int,
    mouseX: Int,
    mouseY: Int,
    x1: Int,
    y1: Int,
    x2: Int,
    y2: Int,
    lineHeight: Int = 12,
    opacity: Float = 0.9f,
    headerColor: Int,
    delta: Float = 0f
) = renderAdvancedTooltip(
    header = header,
    body = splitText(body, targetWidth),
    mouseX = mouseX,
    mouseY = mouseY,
    x1 = x1,
    y1 = y1,
    x2 = x2,
    y2 = y2,
    lineHeight = lineHeight,
    opacity = opacity,
    headerColor = headerColor,
    delta = delta
)

fun GuiGraphics.renderAdvancedTooltip(
    header: MutableComponent,
    body: List<MutableComponent>? = null,
    items: List<ItemStack>? = null,
    mouseX: Int,
    mouseY: Int,
    x1: Int,
    y1: Int,
    x2: Int,
    y2: Int,
    lineHeight: Int = 12,
    opacity: Float = 0.9f,
    headerColor: Int,
    headerOutlineColor: Int = FastColor.ARGB32.multiply(
        headerColor,
        FastColor.ARGB32.color((opacity * 255).toInt(), 192, 192, 192)
    ),
    bodyColor: Int = FastColor.ARGB32.color((255 * opacity).toInt(), 237, 237, 237),
    bodyOutlineColor: Int = bodyColor,
    blur: Float = 1f,
    delta: Float = 0f
) {
    val poseStack = this.pose()
    val font = Minecraft.getInstance().font
    val width = min(max(body?.maxOf { font.width(it) } ?: 0, font.width(header)) + 6, 130)
    val splittedHeader = splitText(header, width)
    val splittedBody = body?.flatMap { splitText(it, width) }
    val headerHeight = splittedHeader.size * lineHeight
    val bodyHeight = (splittedBody?.size ?: 0) * lineHeight
    val itemHeight = (items?.let { if (it.isEmpty()) 0 else 18 + 16 * (it.size * 16 / width) } ?: 0)

    var x = mouseX + 5
    if (x < x1) {
        x += (x1 - x)
    }
    if (x + width > x2) {
        x -= (x + width - x2)
    }

    var y = mouseY + 5
    if (y < y1) {
        y += (y1 - y)
    }
    if (y + headerHeight + bodyHeight + itemHeight > y2) {
        y -= (y + headerHeight + bodyHeight + itemHeight - y2)
    }

    poseStack.pushPose()
    poseStack.translate(0f, 0f, 2000f)
    this.drawBlurredArea(
        x1 = x,
        y1 = y,
        x2 = x + width,
        y2 = y + headerHeight + bodyHeight + itemHeight,
        blur = blur,
        delta = delta
    )
    poseStack.translate(0f, 0f, 2500f)
    this.fillWithOutline(x, y, x + width, y + headerHeight, headerColor, headerOutlineColor)
    this.fillWithOutline(
        x,
        y + headerHeight,
        x + width,
        y + headerHeight + bodyHeight + itemHeight,
        bodyColor,
        bodyOutlineColor
    )

    var lineY = y + 2
    splittedHeader.forEach {
        drawScaledText(
            context = this,
            text = it,
            x = x + 3,
            y = lineY,
            maxCharacterWidth = width - 6,
        )
        lineY += lineHeight
    }
    splittedBody?.forEach {
        drawScaledText(
            context = this,
            text = it,
            x = x + 3,
            y = lineY,
            maxCharacterWidth = width - 6,
            colour = FastColor.ARGB32.color(255, 99, 125, 138)
        )
        lineY += lineHeight
    }
    var itemCount = 0
    items?.forEach {
        this.renderFakeItem(it, x + 3 + itemCount * 16, lineY)
        itemCount++
        if ((itemCount + 1) * 16 > width) {
            lineY += 16
            itemCount = 0
        }
    }
    poseStack.popPose()
}