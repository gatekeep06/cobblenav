package com.metacontent.cobblenav.client.gui.overlay

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.item.Pokefinder
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.FastColor
import net.minecraft.world.phys.AABB
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class PokefinderOverlay : Gui(Minecraft.getInstance()) {
    companion object {
        const val OFFSET: Int = 10
        const val WIDTH: Int = 144
        const val HEIGHT: Int = 96
        const val COMPASS_WIDTH: Int = 23
        const val COMPASS_HEIGHT: Int = 23
        const val COMPASS_OFFSET: Int = 4
        const val RADAR_SCALE: Double = 0.5
        val BACKGROUND = cobblenavResource("textures/gui/pokefinder/overlay.png")
        val COMPASS = cobblenavResource("textures/gui/pokefinder/compass.png")
    }

    private val minecraft = Minecraft.getInstance()

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val isRightHand = minecraft.player?.mainHandItem?.item is Pokefinder
        val scale = minecraft.window.guiScaledWidth.toDouble() / minecraft.window.screenWidth.toDouble() * minecraft.window.guiScale
        val offset = (OFFSET / scale).toInt()
        val width = (WIDTH / scale).toInt()
        val height = (HEIGHT / scale).toInt()
        val compassWidth = (COMPASS_WIDTH / scale).toInt()
        val compassHeight = (COMPASS_HEIGHT / scale).toInt()
        val compassOffset = (COMPASS_OFFSET / scale).toInt()
        val x = if (isRightHand) minecraft.window.guiScaledWidth - width - offset else offset
        val y = minecraft.window.guiScaledHeight - height - offset

        val poseStack = guiGraphics.pose()

        blitk(
            matrixStack = poseStack,
            texture = BACKGROUND,
            x = x,
            y = y,
            width = width,
            height = height
        )

        val player = minecraft.player ?: return

        poseStack.pushPose()
        poseStack.rotateAround(
            Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, 180f - player.rotationVector.y)),
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
        poseStack.popPose()

        val entities = minecraft.level?.getEntitiesOfClass(
            PokemonEntity::class.java,
            AABB.ofSize(player.position(), 500.0, 500.0, 500.0)
        ) {
            it.pokemon.species.name == "Zigzagoon"
        } ?: listOf()

        entities.forEach {
            val vec = player.position().vectorTo(it.position()).scale(RADAR_SCALE)
            poseStack.pushPose()
//            val rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, 180f - player.rotationVector.y))
//            poseStack.rotateAround(rotation, x + 72.5f, y + 48.5f, 0f)
            val angle = Math.toRadians(180.0 - player.rotationVector.y)
            val posX = x + width / 2 + 0.5 + vec.x * cos(angle) - vec.z * sin(angle)
            val posY = y + height / 2 + 0.5 + vec.x * sin(angle) + vec.z * cos(angle)
            poseStack.translate(posX, posY, 0.0)
            guiGraphics.fill(
                -1, -1,
                1, 1,
                FastColor.ARGB32.color(255, 255, 255, 255)
            )
            poseStack.popPose()
        }
    }
}