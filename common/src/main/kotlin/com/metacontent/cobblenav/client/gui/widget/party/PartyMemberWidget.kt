package com.metacontent.cobblenav.client.gui.widget.party

import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import org.joml.Quaternionf
import org.joml.Vector3f

class PartyMemberWidget(
    var x: Int, var y: Int,
    val width: Int, val height: Int,
    val pokemon: RenderablePokemon,
    rotationY: Float
) : Renderable {

    private val rotationVec = Vector3f(0f, rotationY, 0f)

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(
            x.toDouble() + width / 2f,
            y.toDouble() + height / 2f + 18.5 - 1f * 7.5 * 1.5,
            0.0
        )
        drawProfilePokemon(
            renderablePokemon = pokemon,
            matrixStack = poseStack,
            partialTicks = delta,
            rotation = Quaternionf().fromEulerXYZDegrees(rotationVec),
            state = FloatingState(),
            scale = 7.5f,
            applyProfileTransform = false,
            applyBaseScale = true
        )
        poseStack.popPose()
    }
}