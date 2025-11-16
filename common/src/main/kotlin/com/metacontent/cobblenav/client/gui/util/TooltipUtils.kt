package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.FastColor
import net.minecraft.world.item.ItemStack
import kotlin.math.max

fun GuiGraphics.renderSpawnDataTooltip(
    spawnData: SpawnData,
    chanceMultiplier: Float,
    mouseX: Int,
    mouseY: Int,
    x1: Int,
    y1: Int,
    x2: Int,
    y2: Int,
    lineHeight: Int = 12,
    opacity: Float = 0.9f,
    delta: Float = 0f
) {
    val body = mutableListOf<MutableComponent>(
        Component.translatable("gui.cobblenav.spawn_data.spawn_chance", spawnData.spawnChance * chanceMultiplier),
    )

    spawnData.conditions.forEach {
        val value = it.siblings.joinToString(separator = ", ") { sibling -> sibling.string }
        it.siblings.clear()
        it.append(value)
        body.add(it)
    }

    this.renderAdvancedTooltip(
        header = if (spawnData.encountered) spawnData.renderable.species.translatedName else Component.translatable("gui.cobblenav.spawn_data.unknown_pokemon"),
        body = body,
        items = spawnData.blockConditions.asItemStacks,
        mouseX = mouseX,
        mouseY = mouseY,
        x1 = x1,
        y1 = y1,
        x2 = x2,
        y2 = y2,
        lineHeight = lineHeight,
        opacity = opacity,
        headerColor = spawnData.renderable.form.primaryType.hue + ((opacity * 255).toInt() shl 24),
        blur = 1f,
        delta = delta
    )
}

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
    bodyHeight: Int = (body?.let { lineHeight * body.size + 1 } ?: 0),
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
    val width = max(max(body?.maxOf { font.width(it) } ?: 0, font.width(header)) + 6, 80)
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
    if (y + lineHeight + bodyHeight + itemHeight > y2) {
        y -= (y + lineHeight + bodyHeight + itemHeight - y2)
    }

    poseStack.pushPose()
    poseStack.translate(0f, 0f, 2000f)
    this.drawBlurredArea(
        x1 = x,
        y1 = y,
        x2 = x + width,
        y2 = y + lineHeight + bodyHeight + itemHeight,
        blur = blur,
        delta = delta
    )
    poseStack.translate(0f, 0f, 2500f)
    this.fillWithOutline(x, y, x + width, y + lineHeight, headerColor, headerOutlineColor)
    this.fillWithOutline(
        x,
        y + lineHeight,
        x + width,
        y + lineHeight + bodyHeight + itemHeight,
        bodyColor,
        bodyOutlineColor
    )

    drawScaledText(
        context = this,
        text = header,
        x = x + 3,
        y = y + 3,
        maxCharacterWidth = width - 6,
    )
    var lineY = y + lineHeight + 2
    body?.forEach {
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