package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.util.CustomizableBlurEffectProcessor
import com.metacontent.cobblenav.util.cobblenavResource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f

fun GuiGraphics.fillWithOutline(x1: Int, y1: Int, x2: Int, y2: Int, fillColor: Int, outlineColor: Int) {
    this.renderOutline(x1, y1, x2 - x1, y2 - y1, outlineColor)
    this.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillColor)
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
    if (!CobblenavClient.config.enableBlurEffect) return
    this.cobblenavScissor(x1, y1, x2, y2)
    Minecraft.getInstance().gameRenderer.processBlurEffect(blur, delta)
    Minecraft.getInstance().mainRenderTarget.bindWrite(false)
    this.disableScissor()
}

fun GuiGraphics.cobblenavScissor(
    x1: Int,
    y1: Int,
    x2: Int,
    y2: Int,
    scale: Float = CobblenavClient.config.screenScale
) = this.enableScissor(
    (x1 * scale).toInt(),
    (y1 * scale).toInt(),
    (x2 * scale).toInt(),
    (y2 * scale).toInt()
)

fun GameRenderer.processBlurEffect(blur: Float, delta: Float) =
    (this as CustomizableBlurEffectProcessor).`cobblenav$processBlurEffect`(blur, delta)

fun getTimeString(period: IntRange): String =
    String.format("%s - %s", getTimeString(period.first.toLong()), getTimeString(period.last.toLong()))

fun getTimeString(time: Long): String {
    val adjustedTime = (time + 6000) % 23999
    var hours = (adjustedTime / 1000).toInt()
    val minutes = ((adjustedTime % 1000) * 60 / 1000).toInt()
    val period = if (hours >= 12) "PM" else "AM"
    hours %= 12
    hours = if (hours == 0) 12 else hours

    return String.format("%02d:%02d %s", hours, minutes, period)
}

fun translateOr(
    key: String,
    substitute: MutableComponent = Component.literal(key).red()
): Pair<Boolean, MutableComponent> {
    val component = Component.translatable(key)
    if (component.string == key) {
        return Pair(false, substitute)
    }
    return Pair(true, component)
}

fun splitText(text: MutableComponent, targetWidth: Int): List<MutableComponent> {
    return Minecraft.getInstance().font.splitter.splitLines(text, targetWidth, Style.EMPTY)
        .map { Component.literal(it.string).withStyle(text.style) }
}

fun interpolate(start: RGB, end: RGB, progress: Float): RGB {
    return RGB(
        interpolateChannel(start.r, end.r, progress),
        interpolateChannel(start.g, end.g, progress),
        interpolateChannel(start.b, end.b, progress)
    )
}

fun interpolateChannel(start: Int, end: Int, progress: Float): Int {
    return (start + (end - start) * progress).toInt().coerceIn(0, 255)
}

fun dayCycleColor(dayTime: Long, dayColor: RGB, nightColor: RGB): RGB {
    return when (val normalizedTime = dayTime % 24000) {
        in 12040..13670 -> {
            val progress = (normalizedTime - 12040) / 1630f
            interpolate(dayColor, nightColor, progress)
        }

        in 22331..23961 -> {
            val progress = (normalizedTime - 22331) / 1630f
            interpolate(nightColor, dayColor, progress)
        }

        in 13670..22331 -> nightColor
        else -> dayColor
    }
}

fun PoseStack.pushAndPop(
    translate: Vector3d? = null,
    mulPose: Quaternionf? = null,
    scale: Vector3f? = null,
    render: () -> Unit
) {
    this.pushPose()
    translate?.let { this.translate(it.x, it.y, it.z) }
    mulPose?.let { this.mulPose(it) }
    scale?.let { this.scale(it.x, it.y, it.z) }
    render()
    this.popPose()
}

fun gui(path: String) = cobblenavResource("textures/gui/$path.png")