package com.metacontent.cobblenav.client.gui.overlay

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.atan2

class TrackArrowOverlay : Gui(Minecraft.getInstance()) {
    companion object {
        const val OFFSET: Int = 80
        const val ARROW_SIZE: Int = 35
        const val BASE_SIZE: Int = 37
        val ARROW = cobblenavResource("textures/gui/finder/arrow.png")
        val BASE = cobblenavResource("textures/gui/finder/arrow_base.png")
    }

    private val minecraft = Minecraft.getInstance()
    var tracking = false
    var entityId = -1
        set(value) {
            tracking = true
            field = value
        }

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        if (!tracking) return

        val entity = minecraft.level?.getEntity(entityId)
        if (entity == null) {
            tracking = false
            return
        }
        val player = minecraft.player ?: return

        val poseStack = guiGraphics.pose()
        val scale = minecraft.window.guiScaledWidth.toDouble() / minecraft.window.screenWidth.toDouble() * minecraft.window.guiScale
        val offset = (OFFSET / scale).toInt()
        val x = minecraft.window.guiScaledWidth / 2
        val y = minecraft.window.guiScaledHeight - offset
        val arrowSize = (ARROW_SIZE / scale).toInt()
        val baseSize = (BASE_SIZE / scale).toInt()

        val distanceVec = entity.position().vectorTo(player.position())
        val angle = Math.toDegrees(atan2(distanceVec.z, distanceVec.x)).toFloat()

        poseStack.pushPose()
        poseStack.translate(x.toDouble(), y.toDouble(), 0.0)
        blitk(
            matrixStack = poseStack,
            texture = BASE,
            x = -baseSize / 2f,
            y = -baseSize / 2f,
            width = baseSize,
            height = baseSize,
        )
        poseStack.mulPose(Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, 45f + angle - player.yHeadRot)))
        blitk(
            matrixStack = poseStack,
            texture = ARROW,
            x = 0,
            y = -arrowSize,
            width = arrowSize,
            height = arrowSize
        )
        poseStack.popPose()
    }
}