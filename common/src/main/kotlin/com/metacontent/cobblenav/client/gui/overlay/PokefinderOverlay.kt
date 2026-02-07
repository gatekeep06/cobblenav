package com.metacontent.cobblenav.client.gui.overlay

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.item.Pokefinder
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.FastColor
import net.minecraft.world.phys.AABB
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class PokefinderOverlay : Gui(Minecraft.getInstance()) {
    companion object {
        const val WIDTH = 144
        const val HEIGHT = 96
        const val COMPASS_WIDTH = 23
        const val COMPASS_HEIGHT = 23
        const val COMPASS_OFFSET = 4
        const val RADAR_SCALE = 0.5
        const val FILTERED_ALPHA = 0.5
        val DEFAULT_COLOR = FastColor.ARGB32.color(255, 255, 255, 255)
        val SHINY_COLOR = FastColor.ARGB32.color(255, 255, 255, 0)
        val BACKGROUND = gui("pokefinder/overlay")
        val COMPASS = gui("pokefinder/compass")
    }

    private val minecraft = Minecraft.getInstance()
    private var initialized = false
    private val offset = CobblenavClient.config.pokefinderOverlayOffset

    fun initialize() {
        initialized = true
    }

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        if (!initialized) return

        val settings = CobblenavClient.pokefinderSettings

        val isRightHand = minecraft.player?.mainHandItem?.item is Pokefinder
        val scale =
            minecraft.window.guiScaledWidth.toDouble() / minecraft.window.screenWidth.toDouble() * minecraft.window.guiScale
        val scaledOffset = (offset / scale).toInt()
        val scaledWidth = (WIDTH / scale).toInt()
        val scaledHeight = (HEIGHT / scale).toInt()
        val x = if (isRightHand) minecraft.window.guiScaledWidth - scaledWidth - scaledOffset else scaledOffset
        val y = minecraft.window.guiScaledHeight - scaledHeight - scaledOffset

        val poseStack = guiGraphics.pose()

        blitk(
            matrixStack = poseStack,
            texture = BACKGROUND,
            x = x,
            y = y,
            width = scaledWidth,
            height = scaledHeight
        )

        val player = minecraft.player ?: return

        renderCompass(poseStack, 180f - player.rotationVector.y, x, y, scale)

        val radius = /*settings?.radius ?:*/ 200.0
        val entities = settings?.let {
            minecraft.level?.getEntitiesOfClass(
                PokemonEntity::class.java,
                AABB.ofSize(player.position(), radius, radius, radius)
            ) { settings.showFiltered || settings.check(it.pokemon) }
        } ?: listOf()

        entities.forEach {
            val vec = player.position().vectorTo(it.position()).scale(RADAR_SCALE)
            val angle = Math.toRadians(180.0 - player.rotationVector.y)
            val posX = x + scaledWidth / 2 + 0.5 + vec.x * cos(angle) - vec.z * sin(angle)
            val posY = y + scaledHeight / 2 + 0.5 + vec.x * sin(angle) + vec.z * cos(angle)
            var color = DEFAULT_COLOR

            if ((settings?.highlightShiny ?: false) && (it.pokemon.shiny)) {
                color = SHINY_COLOR
            }

            if (!(settings?.check(it.pokemon) ?: true)) {
                color = FastColor.ARGB32.color(
                    (FILTERED_ALPHA*255).toInt(),
                    FastColor.ARGB32.red(color),
                    FastColor.ARGB32.green(color),
                    FastColor.ARGB32.blue(color)
                )
            }

            poseStack.pushAndPop(
                translate = Vector3d(posX, posY, 0.0)
            ) {
                guiGraphics.fill(
                    -1, -1,
                    1, 1,
                    color
                )
                drawScaledText(
                    context = guiGraphics,
                    text = it.pokemon.getDisplayName(),
                    x = 0,
                    y = 2,
                    centered = true,
                    scale = 0.4f,
                    colour = color,
                    //opacity = (FILTERED_ALPHA*100).toInt()
                )
            }
        }
    }

    private fun renderCompass(poseStack: PoseStack, rotation: Float, x: Int, y: Int, scale: Double) {
        val compassWidth = (COMPASS_WIDTH / scale).toInt()
        val compassHeight = (COMPASS_HEIGHT / scale).toInt()
        val compassOffset = (COMPASS_OFFSET / scale).toInt()

        poseStack.pushAndPop {
            poseStack.rotateAround(
                Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, rotation)),
                x + compassOffset + compassWidth / 2f,
                y + compassOffset + compassHeight / 2f,
                0f
            )
            blitk(
                matrixStack = poseStack,
                texture = COMPASS,
                x = x + compassOffset,
                y = y + compassOffset,
                width = compassWidth,
                height = compassHeight
            )
        }
    }
}