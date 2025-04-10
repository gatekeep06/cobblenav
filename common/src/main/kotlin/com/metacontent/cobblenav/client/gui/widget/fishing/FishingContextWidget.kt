package com.metacontent.cobblenav.client.gui.widget.fishing

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.cobblenavScissor
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.joml.Vector2f
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class FishingContextWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    val level: ClientLevel? = Minecraft.getInstance().level
) : SoundlessWidget(x, y, width, height, Component.literal("Weather")) {
    companion object {
        const val SUN_WIDTH = 30
        const val SUN_HEIGHT = 31
        const val HOOK_WIDTH = 10
        const val HOOK_HEIGHT = 12
        const val CLOUD_WIDTH = 30
        const val CLOUD_HEIGHT = 16
        val SUN = cobblenavResource("textures/gui/fishing/sun.png")
        val MOON = cobblenavResource("textures/gui/fishing/moon.png")
        val HOOK = cobblenavResource("textures/gui/fishing/hook.png")
        val CLOUDS = listOf(
            cobblenavResource("textures/gui/fishing/cloud.png")
        )
    }

    private val centerX
        get() = x + (width - SUN_WIDTH) / 2
    private val centerY
        get() = y + height

    private val clouds = mutableListOf<Cloud>()
    private val xRange = -CLOUD_WIDTH..width
    private val yRange = 0..(height - 10 - CLOUD_HEIGHT)

    var lineColor: Int? = null
    var pokeBallStack: ItemStack? = null
    var baitStack: ItemStack? = null

    init {
        val maxCloudNumber = abs(CobblenavClient.config.maxCloudNumber)
        val cloudNumber = ((maxCloudNumber / 2)..maxCloudNumber).random()
        val maxCloudVelocity = abs(CobblenavClient.config.maxCloudVelocity)
        val velocityRange = (maxCloudVelocity / 4)..maxCloudVelocity
        repeat(cloudNumber) {
            val cloudX = xRange.random()
            val cloudY = yRange.random()
            val velocity = intArrayOf(-1, 1).random() * velocityRange.random() / 50f
            clouds.add(Cloud(Vector2f(cloudX.toFloat(), cloudY.toFloat()), velocity, 0))
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()
        guiGraphics.cobblenavScissor(
            x1 = x,
            y1 = y,
            x2 = x + width,
            y2 = y + height
        )

        level?.let {
            val normalizedTime = it.dayTime % 24000
            if (normalizedTime in 23000..24000 || normalizedTime in 0..13702) {
                // There is a small bug with the sun angle that occurs when using `/time set`. I don't know how to fix it
                val sunAngle = 1.5 * PI + ((it.dayTime.toDouble() - 23000) % 24000) / 14702 * PI
                blitk(
                    matrixStack = poseStack,
                    texture = SUN,
                    x = centerX - width / 2 * sin(sunAngle),
                    y = centerY - height * cos(sunAngle),
                    width = SUN_WIDTH,
                    height = SUN_HEIGHT
                )
            }
            if (normalizedTime in 11834..24000 || normalizedTime in 0..167) {
                val moonAngle = 1.5 * PI + ((it.dayTime.toDouble() - 11667) % 24000) / 12333 * PI
                blitk(
                    matrixStack = poseStack,
                    texture = MOON,
                    x = centerX - width / 2 * sin(moonAngle),
                    y = centerY - height * cos(moonAngle),
                    width = SUN_WIDTH,
                    height = SUN_HEIGHT,
                    textureWidth = SUN_WIDTH * 8,
                    uOffset = SUN_WIDTH * it.moonPhase
                )
            }
        }

        poseStack.pushPose()
        poseStack.translate(0f, 0f, -250f)
        pokeBallStack?.let {
            guiGraphics.renderItem(it, x + (width - 16) / 2, y + height - 14)
        }

        guiGraphics.disableScissor()

        baitStack?.let {
            guiGraphics.renderFakeItem(it, x + (width - 16) / 2, y + height + 5)
        }
        poseStack.popPose()

        if (pokeBallStack?.isEmpty == false) {
            blitk(
                matrixStack = poseStack,
                texture = HOOK,
                x = x + (width - HOOK_WIDTH) / 2,
                y = y + height,
                width = HOOK_WIDTH,
                height = HOOK_HEIGHT
            )
        }

        renderClouds(guiGraphics, i, j, f)
    }

    private fun renderClouds(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        clouds.forEach { cloud ->
            blitk(
                matrixStack = guiGraphics.pose(),
                texture = CLOUDS[cloud.type],
                x = x + cloud.position.x,
                y = y + cloud.position.y,
                width = CLOUD_WIDTH,
                height = CLOUD_HEIGHT,
                alpha = 0.8f
            )
            cloud.position.x += cloud.velocity * f
            if (!xRange.contains(cloud.position.x.toInt())) {
                cloud.velocity = -cloud.velocity
            }
        }
    }

    private data class Cloud(
        val position: Vector2f,
        var velocity: Float,
        val type: Int
    )
}