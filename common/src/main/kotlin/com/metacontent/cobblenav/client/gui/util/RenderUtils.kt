package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.util.CustomizableBlurEffectProcessor
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.joml.Quaternionf
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

fun splitText(text: MutableComponent, targetWidth: Int, permissibleDeviation: Int = 10): List<MutableComponent> {
    val font = Minecraft.getInstance().font
    val divided = mutableListOf<MutableComponent>()
    val words = text.string.split(" ")
    var line = Component.empty()
    words.forEach {
        if (font.width(line) + font.width(it) >= targetWidth + permissibleDeviation && line.string.isNotEmpty()) {
            divided.add(line)
            line = Component.empty().append(it)
        }
        else {
            val word = if (line.string.isNotEmpty()) " $it" else it
            line.append(word)
        }
    }
    divided.add(line)
    return divided
}