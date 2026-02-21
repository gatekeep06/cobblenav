package com.metacontent.cobblenav.client.gui.overlay

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.item.Pokefinder
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin

class PokefinderOverlay : Gui(Minecraft.getInstance()) {
    companion object {
        const val WIDTH = 145
        const val HEIGHT = 97
        const val COMPASS_WIDTH = 25
        const val COMPASS_HEIGHT = 25
        const val COMPASS_SHEET_WIDTH = 200
        const val COMPASS_OFFSET = 15
        const val RADIUS = 128.0
        const val RADAR_SCALE = 0.55
        const val DOT_SIZE = 3
        const val COORDINATES_X = 127
        const val COORDINATES_Y = 6.3
        const val COORDINATES_GAP = 6
        const val TEXT_SCALE = 0.4f
        val BACKGROUND = gui("pokefinder/overlay")
        val COMPASS = gui("pokefinder/compass")
        val DOT = gui("pokefinder/dot")
    }

    private val settings = CobblenavClient.pokefinderSettings
    private val minecraft = Minecraft.getInstance()
    private val player = minecraft.player
    private val offset = CobblenavClient.config.pokefinderOverlayOffset

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        player ?: return

        val pos = player.position()

        val isRightHand = player.mainHandItem?.item is Pokefinder
        val x = if (isRightHand) minecraft.window.guiScaledWidth - WIDTH - offset else offset
        val y = minecraft.window.guiScaledHeight - HEIGHT - offset

        val poseStack = guiGraphics.pose()

        blitk(
            matrixStack = poseStack,
            texture = BACKGROUND,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT
        )

        drawScaledText(
            context = guiGraphics,
            text = Component.literal(pos.x.toInt().toString()),
            x = x + COORDINATES_X,
            y = y + COORDINATES_Y,
            scale = TEXT_SCALE,
            maxCharacterWidth = (16 / TEXT_SCALE).toInt()
        )
        drawScaledText(
            context = guiGraphics,
            text = Component.literal(pos.y.toInt().toString()),
            x = x + COORDINATES_X,
            y = y + COORDINATES_Y + COORDINATES_GAP,
            scale = TEXT_SCALE,
            maxCharacterWidth = (16 / TEXT_SCALE).toInt()
        )
        drawScaledText(
            context = guiGraphics,
            text = Component.literal(pos.z.toInt().toString()),
            x = x + COORDINATES_X,
            y = y + COORDINATES_Y + COORDINATES_GAP * 2,
            scale = TEXT_SCALE,
            maxCharacterWidth = (16 / TEXT_SCALE).toInt()
        )

        renderCompass(poseStack, 180f - player.rotationVector.y, x, y)

        val entities = settings?.let {
            minecraft.level?.getEntitiesOfClass(
                PokemonEntity::class.java,
                AABB.ofSize(pos, RADIUS, RADIUS, RADIUS)
            ) { settings.check(it.pokemon) }
        } ?: listOf()

        entities.renderPokemonDots(poseStack, x, y, WIDTH, HEIGHT, pos, player.rotationVector.y)
    }

    private fun renderCompass(poseStack: PoseStack, rotation: Float, x: Int, y: Int) {
        val frame = round((rotation % 360) / 45f).toInt()
        blitk(
            matrixStack = poseStack,
            texture = COMPASS,
            x = x + COMPASS_OFFSET - COMPASS_WIDTH / 2,
            y = y + COMPASS_OFFSET - COMPASS_HEIGHT / 2,
            width = COMPASS_WIDTH,
            height = COMPASS_HEIGHT,
            uOffset = COMPASS_HEIGHT * frame,
            textureWidth = COMPASS_SHEET_WIDTH
        )
    }

    private fun Collection<PokemonEntity>.renderPokemonDots(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        center: Vec3,
        rotation: Float
    ) {
        this.forEach {
            val vec = center.vectorTo(it.position()).scale(RADAR_SCALE)
            val angle = Math.toRadians(180.0 - rotation)
            val dotX = x + width / 2 - DOT_SIZE / 2 + vec.x * cos(angle) - vec.z * sin(angle)
            val dotY = y + height / 2 - DOT_SIZE / 2 + vec.x * sin(angle) + vec.z * cos(angle)
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