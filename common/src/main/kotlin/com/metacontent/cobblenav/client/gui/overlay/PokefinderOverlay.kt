package com.metacontent.cobblenav.client.gui.overlay

import com.cobblemon.mod.common.api.gui.blitk
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
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin

class PokefinderOverlay : Gui(Minecraft.getInstance()) {
    companion object {
        const val WIDTH = 216
        const val HEIGHT = 144
        const val COMPASS_WIDTH = 23
        const val COMPASS_HEIGHT = 23
        const val COMPASS_OFFSET = 4
        const val RADAR_SCALE = 0.8
        const val DOT_SIZE = 4
        val BACKGROUND = gui("pokefinder/overlay")
        val COMPASS = gui("pokefinder/compass")
        val DOT = gui("pokefinder/dot")
    }

    private val settings = CobblenavClient.pokefinderSettings
    private val minecraft = Minecraft.getInstance()
    private val offset = CobblenavClient.config.pokefinderOverlayOffset

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val isRightHand = minecraft.player?.mainHandItem?.item is Pokefinder
        val scale =
            minecraft.window.guiScaledWidth.toDouble() / minecraft.window.screenWidth.toDouble() * minecraft.window.guiScale / CobblenavClient.config.screenScale
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
            ) { settings.check(it.pokemon) }
        } ?: listOf()

        entities.renderPokemonDots(poseStack, x, y, scaledWidth, scaledHeight, player.position(), player.rotationVector.y, scale)
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

    private fun Collection<PokemonEntity>.renderPokemonDots(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        center: Vec3,
        rotation: Float,
        scale: Double
    ) {
        this.forEach {
            val vec = center.vectorTo(it.position()).scale(RADAR_SCALE / scale)
            val angle = Math.toRadians(180.0 - rotation)
            val dotX = x + width / 2 + vec.x * cos(angle) - vec.z * sin(angle)
            val dotY = y + height / 2 + vec.x * sin(angle) + vec.z * cos(angle)
            blitk(
                matrixStack = poseStack,
                texture = DOT,
                x = floor(dotX),
                y = floor(dotY),
                width = DOT_SIZE,
                height = DOT_SIZE
            )
        }
    }
}