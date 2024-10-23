package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.metacontent.cobblenav.client.gui.util.AnimationTimer
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.util.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class FinderScreen(
    private val spawnData: SpawnData,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean= false
) : PokenavScreen(makeOpeningSound, animateOpening, Component.literal("Finder")) {
    companion object {
        const val CLOSING_DURATION: Float = 3f
        const val FADING_DURATION: Float = 5f
        const val POKEBALL_PART_WIDTH: Int = 308
        const val POKEBALL_PART_HEIGHT: Int = 134
        const val POKEMON_OFFSET: Int = 45
        const val SCALE: Float = 40f
        val POKEBALL_TOP = cobblenavResource("textures/gui/pokeball_screen_top.png")
        val POKEBALL_BOTTOM = cobblenavResource("textures/gui/pokeball_screen_bottom.png")
    }

    override val color = FastColor.ARGB32.color(255, 190, 72, 72)
    private val closingTimer = AnimationTimer(CLOSING_DURATION)
    private val fadingTimer = AnimationTimer(FADING_DURATION)
    private val state = FloatingState()
    private var pokemonX = 0f
    private var pokemonY = 0f

    override fun initScreen() {
        pokemonX = screenX + WIDTH / 2f
        pokemonY = screenY + HEIGHT / 2f - POKEMON_OFFSET - if (spawnData.pose == PoseType.SWIM) 10 else 0
    }

    override fun renderScreen(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()

        // i actually cannot understand how these layouts work, but they do and do fine
        drawPokemon(
            poseStack = poseStack,
            pokemon = spawnData.pokemon,
            x = pokemonX,
            y = pokemonY,
            z = 100f,
            delta = delta,
            state = state,
            poseType = if (spawnData.pose == PoseType.PROFILE) PoseType.WALK else spawnData.pose,
            scale = SCALE,
            obscured = !spawnData.encountered
        )

        if (!fadingTimer.isOver()) {
            renderPokeballAnimation(guiGraphics, mouseX, mouseY, delta)
            closingTimer.tick(delta)
            if (!closingTimer.isOver()) return
            fadingTimer.tick(delta)
        }
    }

    private fun renderPokeballAnimation(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()

        poseStack.pushPose()
        if (!closingTimer.isOver()) previousScreen?.render(guiGraphics, mouseX, mouseY, delta)
        poseStack.translate(0f, 0f, 400f)
        blitk(
            matrixStack = poseStack,
            texture = POKEBALL_BOTTOM,
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - closingTimer.getProgress() * POKEBALL_PART_HEIGHT,
            width = POKEBALL_PART_WIDTH,
            height = POKEBALL_PART_HEIGHT,
            alpha = 1 - fadingTimer.getProgress()
        )
        blitk(
            matrixStack = poseStack,
            texture = POKEBALL_TOP,
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH - POKEBALL_PART_HEIGHT + closingTimer.getProgress() * POKEBALL_PART_HEIGHT,
            width = POKEBALL_PART_WIDTH,
            height = POKEBALL_PART_HEIGHT,
            alpha = 1f - fadingTimer.getProgress()
        )
        poseStack.popPose()
    }
}