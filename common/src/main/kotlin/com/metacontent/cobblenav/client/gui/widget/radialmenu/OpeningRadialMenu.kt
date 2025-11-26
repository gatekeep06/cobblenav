package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.stateful.StatefulWidget
import com.metacontent.cobblenav.os.PokenavOS
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class OpeningRadialMenu(
    os: PokenavOS,
    statefulWidget: StatefulWidget,
    pX: Int, pY: Int
) : RadialMenuState(os, statefulWidget, pX, pY, DIAMETER, DIAMETER, Component.literal("Opening Radial Menu")) {
    companion object {
        const val ANIMATION_DURATION = 3f
        const val ROTATION = 180f
        const val DIAMETER = 20
    }

    private val frameAmount: Int = ANIMATION_SHEET_WIDTH / DIAMETER
    private val timer = Timer(ANIMATION_DURATION)
    private val buttons = listOf(SWITCH_OFF, CONTACTS, LOCATION, MAP)

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        timer.tick(delta)
        val progress = timer.getProgress()
        val poseStack = guiGraphics.pose()
        val animY = y - DIAMETER / 2 * progress
        poseStack.pushAndPop {
            poseStack.rotateAround(
                Axis.ZP.rotationDegrees(ROTATION * progress),
                (x + DIAMETER / 2).toFloat(),
                animY + DIAMETER / 2,
                0f
            )
            blitk(
                poseStack,
                RADIAL_MENU,
                x, animY,
                height = DIAMETER,
                width = DIAMETER,
                uOffset = DIAMETER * ((frameAmount - 1) * progress).toInt(),
                textureWidth = ANIMATION_SHEET_WIDTH,
                red = 1.1, green = 1.1, blue = 1.1
            )
        }

        val iterator = buttons.iterator()
        var buttonIndex = 0.5
        while (iterator.hasNext()) {
            val button = iterator.next()
            val angle = buttonIndex / buttons.size.toFloat() * PI
            blitk(
                poseStack, button,
                //plus 1 to make it more symmetrical
                x + 1 - progress * 40 * cos(angle),
                animY - progress * 40 * sin(angle),
                height = 16, width = 16
            )
            buttonIndex++
        }

        if (timer.isOver()) {
            statefulWidget.changeState(OpenedRadialMenu(os, statefulWidget, x, y))
        }
    }

    override val blockScreenWidgets: Boolean = true


}