package com.metacontent.cobblenav.client.gui.overlay

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.*

class TrackArrowOverlay : Gui(Minecraft.getInstance()) {
    private val minecraft = Minecraft.getInstance()
    private val offset = CobblenavClient.config.trackArrowYOffset
    private val stack by lazy { ItemStack(CobblemonItems.POKE_BALL) }
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
        val scale =
            minecraft.window.guiScaledWidth.toDouble() / minecraft.window.screenWidth.toDouble() * minecraft.window.guiScale
        val scaledOffset = (offset / scale).toInt()
        val x = minecraft.window.guiScaledWidth / 2
        val y = minecraft.window.guiScaledHeight - scaledOffset

        val distanceVec = player.position().vectorTo(entity.position())
        val yaw = atan2(distanceVec.z, distanceVec.x).toFloat()
        val horizontalDistance = sqrt(distanceVec.x * distanceVec.x + distanceVec.z * distanceVec.z)
        val pitch = atan2(distanceVec.y, horizontalDistance).toFloat()

        poseStack.pushPose()
        poseStack.translate(x.toDouble(), y.toDouble(), 0.0)
        poseStack.mulPose(
            Quaternionf()
                .rotateZ(PI.toFloat())
                .fromEulerXYZDegrees(Vector3f(player.xRot, -player.yRot, 0f))
                .rotateY(0.5f * PI.toFloat() + yaw)
                .rotateX(-pitch)
        )
        poseStack.scale(50f, 50f, -50f)
        minecraft.itemRenderer.renderStatic(
            stack,
            ItemDisplayContext.GROUND,
            255,
            1000,
            poseStack,
            guiGraphics.bufferSource(),
            minecraft.level,
            0
        )
        poseStack.popPose()

        drawScaledText(
            context = guiGraphics,
            text = Component.translatable("gui.cobblenav.finder.distance", distanceVec.length().toInt()),
            x = x,
            y = y + 20,
            centered = true
        )
    }
}