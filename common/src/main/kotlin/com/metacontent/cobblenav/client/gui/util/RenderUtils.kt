package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.util.CustomizableBlurEffectProcessor
import com.metacontent.cobblenav.util.SpawnData
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.FastColor
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.ceil
import kotlin.math.max

fun GuiGraphics.fillWithOutline(x1: Int, y1: Int, x2: Int, y2: Int, fillColor: Int, outlineColor: Int) {
    this.renderOutline(x1, y1, x2 - x1, y2 - y1, outlineColor)
    this.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillColor)
}

fun GuiGraphics.renderSpawnDataTooltip(
    spawnData: SpawnData,
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
    val body = mutableListOf(
        Component.translatable("gui.cobblenav.spawn_data.spawn_chance", spawnData.spawnChance.toString()),
        Component.translatable("gui.cobblenav.spawn_data.encountered", spawnData.encountered.toString()),
        Component.translatable("gui.cobblenav.spawn_data.biome", Component.translatable(String.format("%s.%s.%s", "biome", spawnData.biome.namespace, spawnData.biome.path)).string)
    )
    if (TimeRange.timeRanges["any"]?.ranges?.contains(spawnData.time) == false) {
        body.add(Component.translatable("gui.cobblenav.spawn_data.time", getTimeString(spawnData.time)))
    }
    if (spawnData.additionalConditions.isNotEmpty()) {
        val conditionsComponent = Component.translatable("gui.cobblenav.spawn_data.conditions")
        spawnData.additionalConditions.forEach {
            conditionsComponent.append(" ").append(Component.translatable("condition.cobblenav.$it"))
        }
        body.add(conditionsComponent)
    }

    this.renderAdvancedTooltip(
        header = if (spawnData.encountered) spawnData.pokemon.species.translatedName else Component.translatable("gui.cobblenav.spawn_data.unknown_pokemon"),
        body = body,
        mouseX = mouseX,
        mouseY = mouseY,
        x1 = x1,
        y1 = y1,
        x2 = x2,
        y2 = y2,
        lineHeight = lineHeight,
        opacity = opacity,
        headerColor = spawnData.pokemon.form.primaryType.hue + ((opacity * 255).toInt() shl 24),
        blur = 1f,
        delta = delta
    )
}

fun GuiGraphics.renderMultilineTextTooltip(
    header: MutableComponent,
    body: MutableComponent,
    targetWidth: Int,
    permissibleDeviation: Int = 10,
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
) {
    val font = Minecraft.getInstance().font
    val multilineBody = mutableListOf<MutableComponent>()
    val words = body.string.split(" ")
    var line = Component.empty()
    words.forEach {
        val alteredLine = line.copy()
        alteredLine.append("$it ")
        if (font.width(alteredLine) >= targetWidth + permissibleDeviation && line.string.isNotEmpty()) {
            multilineBody.add(line)
            line = Component.empty().append("$it ")
        }
        else {
            line = alteredLine
        }
    }
    multilineBody.add(line)

    renderAdvancedTooltip(
        header = header,
        body = multilineBody,
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
}

fun GuiGraphics.renderAdvancedTooltip(
    header: MutableComponent,
    body: List<MutableComponent>?,
    mouseX: Int,
    mouseY: Int,
    x1: Int,
    y1: Int,
    x2: Int,
    y2: Int,
    lineHeight: Int = 12,
    opacity: Float = 0.9f,
    headerColor: Int,
    headerOutlineColor: Int = FastColor.ARGB32.multiply(headerColor, FastColor.ARGB32.color((opacity * 255).toInt(), 192, 192, 192)),
    bodyColor: Int = FastColor.ARGB32.color((255 * opacity).toInt(), 237, 237, 237),
    bodyOutlineColor: Int = bodyColor,
    blur: Float = 1f,
    delta: Float = 0f
) {
    val poseStack = this.pose()
    val font = Minecraft.getInstance().font
    val width = max(max(body?.maxOf { font.width(it) } ?: 0, font.width(header)) + 6, 80)
    val bodyHeight = body?.let { lineHeight * body.size + 1 } ?: 0

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
    if (y + lineHeight + bodyHeight > y2) {
        y -= (y + lineHeight + bodyHeight - y2)
    }

    poseStack.pushPose()
    poseStack.translate(0f, 0f, 2000f)
    this.drawBlurredArea(x, y, x + width, y + lineHeight + bodyHeight, blur, delta)
    this.fillWithOutline(x, y, x + width, y + lineHeight, headerColor, headerOutlineColor)
    this.fillWithOutline(x, y + lineHeight, x + width, y + lineHeight + bodyHeight, bodyColor, bodyOutlineColor)

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
    poseStack.popPose()
}

fun drawPokemon(
    poseStack: PoseStack,
    pokemon: RenderablePokemon,
    x: Float = 0f,
    y: Float = 0f,
    z: Float = 0f,
    delta: Float = 0f,
    state: PosableState,
    poseType: PoseType = PoseType.PROFILE,
    scale: Float = 15f,
    rotation: Quaternionf = Quaternionf().fromEulerXYZDegrees(Vector3f(13F, 35F, 0F)),
    obscured: Boolean = false
) {
    val rgb = if (obscured) 0f else 1f
    poseStack.pushPose()
    poseStack.translate(x, y, z)
    drawProfilePokemon(
        renderablePokemon = pokemon,
        matrixStack = poseStack,
        partialTicks = delta,
        rotation = rotation,
        state = state,
        scale = scale,
        poseType = poseType,
        r = rgb,
        g = rgb,
        b = rgb
    )
    poseStack.popPose()
}

fun GuiGraphics.drawBlurredArea(
    x1: Int,
    y1: Int,
    x2: Int,
    y2: Int,
    blur: Float = 1f,
    delta: Float
) {
    this.enableScissor(x1, y1, x2, y2)
    Minecraft.getInstance().gameRenderer.processBlurEffect(blur, delta)
    Minecraft.getInstance().mainRenderTarget.bindWrite(false)
    this.disableScissor()
}

fun GameRenderer.processBlurEffect(blur: Float, delta: Float) = (this as CustomizableBlurEffectProcessor).`cobblenav$processBlurEffect`(blur, delta)

fun getTimeString(period: IntRange): String = String.format("%s - %s", getTimeString(period.first.toLong()), getTimeString(period.last.toLong()))

fun getTimeString(time: Long): String {
    val adjustedTime = (time + 6000) % 23999
    var hours = (adjustedTime / 1000).toInt()
    val minutes = ((adjustedTime % 1000) * 60 / 1000).toInt()
    val period = if (hours >= 12) "PM" else "AM"
    hours %= 12
    hours = if (hours == 0) 12 else hours

    return String.format("%02d:%02d %s", hours, minutes, period)
}