package com.metacontent.cobblenav.client.gui.widget.finder

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.spawning.condition.SubmergedSpawningCondition
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
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
        const val BAR_LENGTH: Int = 12
        const val BARS: Int = 36
        const val NOTIFICATION_WIDTH: Int = 14
        const val NOTIFICATION_HEIGHT: Int = 15
        const val NOTIFICATION_OFFSET: Int = 30
        const val SHINY_ASPECT = "shiny"
        val DECORATIONS_1 = cobblenavResource("textures/gui/finder/finder_decorations_1.png")
        val DECORATIONS_2 = cobblenavResource("textures/gui/finder/finder_decorations_2.png")
        val NOTIFICATION = cobblenavResource("textures/gui/finder/shiny_notification.png")
    }

    private val pose = if (spawnData.spawningContext == SubmergedSpawningCondition.NAME && CobblenavClient.config.useSwimmingAnimationIfSubmerged)
        PoseType.SWIM else PoseType.WALK
    private val state = FloatingState()
    private val openingTimer = Timer(OPENING)
    private val loopTimer = Timer(LOOP, true)
    private val obscured = !spawnData.known() && CobblenavClient.config.obscureUnknownPokemon

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        val poseStack = guiGraphics.pose()

        poseStack.pushPose()
        poseStack.rotateAround(Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, 360f * (loopTimer.getProgress() + (1f - openingTimer.getProgress())))), x.toFloat(), y.toFloat(), 0f)
        for (barIndex in 0 until (BARS * openingTimer.getProgress()).toInt()) {
            poseStack.pushPose()
            poseStack.rotateAround(Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, barIndex * 360f / BARS)), x.toFloat(), y.toFloat(), 0f)
            guiGraphics.fill(x - 2, y + RADIUS, x + 2, y + RADIUS + BAR_LENGTH, FastColor.ARGB32.color(128, 173, 232, 244))
            poseStack.popPose()
        }
        poseStack.popPose()
        poseStack.pushPose()
        poseStack.rotateAround(Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, -360f * (loopTimer.getProgress() + (1f - openingTimer.getProgress())))), x.toFloat(), y.toFloat(), 0f)
        blitk(
            matrixStack = poseStack,
            texture = DECORATIONS_1,
            x = x - RADIUS - BAR_LENGTH - 32,
            y = y - RADIUS - BAR_LENGTH - 32,
            width = 2 * (RADIUS + BAR_LENGTH + 32),
            height = 2 * (RADIUS + BAR_LENGTH + 32),
            alpha = 0.8f
        )
        blitk(
            matrixStack = poseStack,
            texture = DECORATIONS_2,
            x = x - RADIUS + 4,
            y = y - RADIUS + 4,
            width = 2 * (RADIUS - 4),
            height = 2 * (RADIUS - 4),
            alpha = 0.8f
        )
        poseStack.popPose()

        drawPokemon(
            poseStack = poseStack,
            pokemon = spawnData.renderable,
            x = x.toFloat(),
            y = y.toFloat() - POKEMON_OFFSET, //- if (spawnData.pose == PoseType.SWIM) 10 else 0,
            z = 100f,
            delta = delta,
            state = state,
            poseType = pose,
            scale = SCALE,
            rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(25F, 35F, 0F)),
            obscured = obscured
        )

        if (pokemon.aspects.contains(SHINY_ASPECT)) {
            blitk(
                matrixStack = poseStack,
                texture = NOTIFICATION,
                x = x + RADIUS - NOTIFICATION_OFFSET - NOTIFICATION_WIDTH / 2,
                y = y - RADIUS + NOTIFICATION_OFFSET,
                width = NOTIFICATION_WIDTH,
                height = NOTIFICATION_HEIGHT
            )
        }

        openingTimer.tick(delta)
        loopTimer.tick(delta)
    }
}