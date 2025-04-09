package com.metacontent.cobblenav.client.gui.widget.fishing

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.util.cobblenavScissor
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class FishingContextWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    val level: ClientLevel? = Minecraft.getInstance().level
) : SoundlessWidget(x, y, width, height, Component.literal("Weather")) {
    companion object {
        const val SUN_WIDTH = 30
        const val SUN_HEIGHT = 31
        const val HOOK_WIDTH = 10
        const val HOOK_HEIGHT = 12
        val SUN = cobblenavResource("textures/gui/fishing/sun.png")
        val MOON = cobblenavResource("textures/gui/fishing/moon.png")
        val HOOK = cobblenavResource("textures/gui/fishing/hook.png")
    }

    private val centerX
        get() = x + (width - SUN_WIDTH) / 2
    private val centerY
        get() = y + height

    var lineColor: Int? = null
    var pokeBallStack: ItemStack? = null
    var baitStack: ItemStack? = null

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()
        guiGraphics.cobblenavScissor(
            x1 = x,
            y1 = y,
            x2 = x + width,
            y2 = y + height
        )

        level?.let {
            val normalizedTime = it.dayTime % 24000
            if (normalizedTime in 23000..24000 || normalizedTime in 0..13702) {
                // There is a small bug with the sun angle that occurs when using `/time set`. I don't know how to fix it
                val sunAngle = 1.5 * PI + ((it.dayTime.toDouble() - 23000) % 24000) / 14702 * PI
                blitk(
                    matrixStack = poseStack,
                    texture = SUN,
                    x = centerX - width / 2 * sin(sunAngle),
                    y = centerY - height * cos(sunAngle),
                    width = SUN_WIDTH,
                    height = SUN_HEIGHT
                )
            }
            if (normalizedTime in 11834..24000 || normalizedTime in 0..167) {
                val moonAngle = 1.5 * PI + ((it.dayTime.toDouble() - 11667) % 24000) / 12333 * PI
                blitk(
                    matrixStack = poseStack,
                    texture = MOON,
                    x = centerX - width / 2 * sin(moonAngle),
                    y = centerY - height * cos(moonAngle),
                    width = SUN_WIDTH,
                    height = SUN_HEIGHT,
                    textureWidth = SUN_WIDTH * 8,
                    uOffset = SUN_WIDTH * it.moonPhase
                )
            }
        }

        poseStack.pushPose()
        poseStack.translate(0f, 0f, -250f)
        pokeBallStack?.let {
            guiGraphics.renderItem(it, x + (width - 16) / 2, y + height - 14)
        }

        guiGraphics.disableScissor()

        baitStack?.let {
            guiGraphics.renderFakeItem(it, x + (width - 16) / 2, y + height + 5)
        }
        poseStack.popPose()

        if (pokeBallStack?.isEmpty == false) {
            blitk(
                matrixStack = poseStack,
                texture = HOOK,
                x = x + (width - HOOK_WIDTH) / 2,
                y = y + height,
                width = HOOK_WIDTH,
                height = HOOK_HEIGHT
            )
        }
    }
}