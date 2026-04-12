package com.metacontent.cobblenav.client.gui.widget.fishing

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.api.generalresources.CloudRepository
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.*
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3d
import org.joml.Vector3f
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class FishingContextWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    val level: ClientLevel? = Minecraft.getInstance().level
) : SoundlessWidget(x, y, width, height, Component.literal("Weather")) {
    companion object {
        const val SUN_WIDTH = 20
        const val SUN_HEIGHT = 21
        const val HOOK_WIDTH = 10
        const val HOOK_HEIGHT = 10
        const val CLOUD_WIDTH = 40
        const val CLOUD_HEIGHT = 21
        const val MAX_CLOUD_OPACITY = 0.95f
        const val MAX_STARS_OPACITY = 1f
        const val WINGULL_CHANCE = 6
        private val DAY_COLOR = RGB(115, 215, 255)
        private val NIGHT_COLOR = RGB(2, 1, 39)
        val SUN = gui("fishing/sun")
        val MOON = gui("fishing/moon")
        val HOOK = gui("fishing/hook")
        val STARS = gui("fishing/stars")
    }

    private val centerX
        get() = x + (width - SUN_WIDTH) / 2
    private val centerY
        get() = y + height

    private val color: Int
        get() = dayCycleColor(level?.dayTime ?: 0L, DAY_COLOR, NIGHT_COLOR).toColor()
    private val cloudTextures = CloudRepository.clouds.toList()
    private val clouds = mutableListOf<Cloud>()
    private val xRange = -CLOUD_WIDTH..width
    private val yRange = 0..(height - 10 - CLOUD_HEIGHT)
    private val displayWingull = Random.nextInt(100) <= WINGULL_CHANCE
    private val wingull by lazy {
        PokemonProperties.parse("species=wingull").asRenderablePokemon()
    }
    private val wingullState by lazy { FloatingState() }
    private var wingullPosition = 0f
    private var wingullVelocity = 0.2f
    private var cloudOpacity = 0f
    private var starsOpacity = 0f

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

        guiGraphics.fill(
            x,
            y,
            x + width,
            y + height,
            color
        )

        blitk(
            matrixStack = poseStack,
            texture = STARS,
            x = x,
            y = y,
            width = width,
            height = height,
            textureWidth = 308,
            textureHeight = 70,
            alpha = starsOpacity
        )

        level?.let {
            val adjustedTime = (it.dayTime) % 24000
            val sunAngle = Math.toRadians(adjustedTime / 24000.0 * 360.0 + 180.0) % 360
            val moonAngle = Math.toRadians(adjustedTime / 24000.0 * 360.0)
            blitk(
                matrixStack = poseStack,
                texture = SUN,
                x = centerX + width / 2 * cos(sunAngle),
                y = centerY + height * sin(sunAngle),
                width = SUN_WIDTH,
                height = SUN_HEIGHT
            )
            blitk(
                matrixStack = poseStack,
                texture = MOON,
                x = centerX + width / 2 * cos(moonAngle),
                y = centerY + height * sin(moonAngle),
                width = SUN_WIDTH,
                height = SUN_HEIGHT,
                textureWidth = SUN_WIDTH * 8,
                uOffset = SUN_WIDTH * it.moonPhase
            )
            if (adjustedTime in 23000..24000 || adjustedTime in 0..13702) {

                if (cloudOpacity < MAX_CLOUD_OPACITY) cloudOpacity =
                    (cloudOpacity + 0.02f).coerceIn(0f, MAX_CLOUD_OPACITY)
                if (starsOpacity > 0) starsOpacity = (starsOpacity - 0.02f).coerceIn(0f, MAX_STARS_OPACITY)
            }
            if (adjustedTime in 11834..24000 || adjustedTime in 0..167) {

                if (cloudOpacity > 0) cloudOpacity = (cloudOpacity - 0.01f).coerceIn(0f, MAX_CLOUD_OPACITY)
                if (starsOpacity < MAX_STARS_OPACITY) starsOpacity =
                    (starsOpacity + 0.01f).coerceIn(0f, MAX_STARS_OPACITY)
            }
        }

        pokeBallStack?.let {
            guiGraphics.renderItem(it, x + (width - 16) / 2, y + height - 14)
        }

        guiGraphics.disableScissor()

        baitStack?.let {
            poseStack.pushAndPop(
                scale = Vector3f(0.6f, 0.6f, 0.6f)
            ) {
                RenderSystem.setShaderColor(0.4f, 0.4f, 1f, 1f)
                guiGraphics.renderFakeItem(
                    it,
                    ((x + (width - 9.6) / 2) / 0.6).toInt(),
                    ((y + height + 3) / 0.6).toInt()
                )
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            }
        }

        if (pokeBallStack?.isEmpty == false) {
            poseStack.pushAndPop(
                translate = Vector3d(0.0, 0.0, 50.0)
            ) {
                blitk(
                    matrixStack = poseStack,
                    texture = HOOK,
                    x = x + (width - HOOK_WIDTH) / 2,
                    y = y + height - 1,
                    width = HOOK_WIDTH,
                    height = HOOK_HEIGHT
                )
            }
        }

        renderClouds(guiGraphics, i, j, f)

        if (displayWingull) {
            if (wingullPosition > 3 * width || wingullPosition < -2 * width) {
                wingullVelocity = -wingullVelocity
            }
            drawPokemon(
                poseStack = poseStack,
                pokemon = wingull,
                x = x - 10 + wingullPosition,
                y = y + 10f,
                z = 10f,
                delta = f,
                state = wingullState,
                poseType = PoseType.FLY,
                scale = 5f,
                rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(0f, if (wingullVelocity > 0) 270f else 90f, 0f))
            )
            wingullPosition += wingullVelocity
        }
    }

    private fun renderClouds(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        clouds.forEach { cloud ->
            blitk(
                matrixStack = guiGraphics.pose(),
                texture = cloudTextures.getOrNull(cloud.type),
                x = x + cloud.position.x,
                y = y + cloud.position.y,
                width = CLOUD_WIDTH,
                height = CLOUD_HEIGHT,
                alpha = cloudOpacity
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