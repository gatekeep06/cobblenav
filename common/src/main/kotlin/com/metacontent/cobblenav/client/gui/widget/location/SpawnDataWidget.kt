package com.metacontent.cobblenav.client.gui.widget.location

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.metacontent.cobblenav.client.gui.screen.FinderScreen
import com.metacontent.cobblenav.client.gui.screen.LocationScreen
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.client.gui.widget.ClickableParentWidget
import com.metacontent.cobblenav.util.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class SpawnDataWidget(
    x: Int, y: Int,
    val spawnData: SpawnData,
    private val parent: LocationScreen
) : ClickableParentWidget(x, y, WIDTH, HEIGHT, Component.literal("Spawn Data Widget")) {
    companion object {
        const val WIDTH: Int = 40
        const val HEIGHT: Int = 50
        const val MODEL_HEIGHT: Int = 40
        val format= DecimalFormat("#.##")
        val BACKGROUND = cobblenavResource("textures/gui/pokeball_background.png")
    }

    private val state = FloatingState()

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        if (isHovered) {
            blitk(
                matrixStack = poseStack,
                texture = BACKGROUND,
                x = x + 2,
                y = y + 2,
                width = MODEL_HEIGHT - 4,
                height = MODEL_HEIGHT - 4,
                alpha = 0.5f
            )
            parent.hoveredSpawnData = spawnData
        }
        drawPokemon(
            poseStack = poseStack,
            pokemon = spawnData.pokemon,
            x = x.toFloat() + width / 2,
            y = y.toFloat() + if (spawnData.pose == PoseType.PROFILE) 8 else 0,
            z = 100f,
            delta = delta,
            state = state,
            poseType = spawnData.pose,
            obscured = !spawnData.encountered
        )
        drawScaledText(
            guiGraphics,
            text = Component.literal(format.format(spawnData.spawnChance) + "%"),
            x = x + width / 2, y = y + MODEL_HEIGHT,
            maxCharacterWidth = width,
            centered = true,
            shadow = true
        )
    }

    override fun onClick(d: Double, e: Double) {
        parent.changeScreen(FinderScreen(spawnData), true)
    }
}