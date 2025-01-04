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
    val pokemon: RenderablePokemon,
    private val scale: Float,
    rotationY: Float
) : Renderable {

    private val rotationVec = Vector3f(0f, rotationY, 0f)
    private val state = FloatingState()

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        val baseScale = pokemon.form.baseScale
        poseStack.pushPose()
        poseStack.translate(
            x.toDouble(),
            y.toDouble() + 18.5 - baseScale * scale * 1.5,
            0.0
        )
        drawProfilePokemon(
            renderablePokemon = pokemon,
            matrixStack = poseStack,
            partialTicks = delta,
            rotation = Quaternionf().fromEulerXYZDegrees(rotationVec),
            state = state,
            scale = scale,
            applyProfileTransform = false,
            applyBaseScale = true
        )
        poseStack.popPose()
    }
}