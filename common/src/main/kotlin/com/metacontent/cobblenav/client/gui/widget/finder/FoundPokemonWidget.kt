package com.metacontent.cobblenav.client.gui.widget.finder

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.util.SpawnData
import com.metacontent.cobblenav.util.finder.FoundPokemon
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import org.joml.Quaternionf
import org.joml.Vector3f

class FoundPokemonWidget(
    x: Int, y: Int,
    val spawnData: SpawnData,
    val pokemon: FoundPokemon
) : SoundlessWidget(x, y, 0, 0, Component.literal("Found Pokemon")) {
    companion object {
        const val RADIUS: Int = 60
        const val POKEMON_OFFSET: Int = 45
        const val SCALE: Float = 40f
        const val OPENING: Float = 12f
        const val LOOP: Float = 5000f
        const val BARS: Int = 36
    }

    private val state = FloatingState()
    private val openingTimer = Timer(OPENING)
    private val loopTimer = Timer(LOOP, true)

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        if (!pokemon.found) return

        val poseStack = guiGraphics.pose()

        poseStack.pushPose()
        poseStack.rotateAround(Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, 360f * loopTimer.getProgress())), x.toFloat(), y.toFloat(), 0f)
        for (barIndex in 0 until (BARS * openingTimer.getProgress()).toInt()) {
            poseStack.pushPose()
            poseStack.rotateAround(Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, barIndex * 360f / BARS)), x.toFloat(), y.toFloat(), 0f)
            guiGraphics.fill(x - 2, y + RADIUS, x + 2, y + RADIUS + 12, FastColor.ARGB32.color(128, 0, 0, 0))
            poseStack.popPose()
        }
        poseStack.popPose()

        drawPokemon(
            poseStack = poseStack,
            pokemon = spawnData.pokemon,
            x = x.toFloat(),
            y = y.toFloat() - POKEMON_OFFSET - if (spawnData.pose == PoseType.SWIM) 10 else 0,
            z = 100f,
            delta = delta,
            state = state,
            poseType = if (spawnData.pose == PoseType.PROFILE) PoseType.WALK else spawnData.pose,
            scale = SCALE,
            rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(25F, 35F, 0F)),
            obscured = !spawnData.encountered
        )

        loopTimer.tick(delta)
        openingTimer.tick(delta)
    }
}