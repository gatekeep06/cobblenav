package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.util.SpawnData
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import org.joml.Quaternionf
import org.joml.Vector3f
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
    headerColor: Int = spawnData.pokemon.form.primaryType.hue + ((opacity * 255).toInt() shl 24),
    headerOutlineColot: Int = FastColor.ARGB32.multiply(headerColor, FastColor.ARGB32.color((opacity * 255).toInt(), 192, 192, 192)),
    bodyColor: Int = FastColor.ARGB32.color((255 * opacity).toInt(), 237, 237, 237),
    bodyOutlineColor: Int = bodyColor
) {
    val poseStack = this.pose()

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
    val font = Minecraft.getInstance().font
    val width = max(font.width(body.maxBy { font.width(it) }) + 6, 80)
    val bodyHeight = lineHeight * body.size + 1 //+ if (spawnData.neededBlocksAsItems!!.isNotEmpty()) 16 else 0

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
    this.fillWithOutline(x, y, x + width, y + lineHeight, headerColor, headerOutlineColot)
    this.fillWithOutline(x, y + lineHeight, x + width, y + lineHeight + bodyHeight, bodyColor, bodyOutlineColor)

    drawScaledText(
        context = this,
        text = if (spawnData.encountered) spawnData.pokemon.species.translatedName else Component.translatable("gui.cobblenav.spawn_data.unknown_pokemon"),
        x = x + 3,
        y = y + 3,
        maxCharacterWidth = width - 6,
    )
    var lineY = y + lineHeight + 2
    body.forEach {
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

//    var blockX = x + 3
//    lineY -= 2
//    val blockSpace = 10
//    spawnData.neededBlocksAsItems!!.forEach {
//        this.renderFakeItem(it, blockX, lineY)
//        blockX += blockSpace
//    }
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