package com.metacontent.cobblenav.client.gui.widget

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.FastColor
import org.joml.Quaternionf
import org.joml.Vector3f

class NotificationWidget(pX: Int, pY: Int) :
    SoundlessWidget(pX, pY, WIDTH, HEIGHT, Component.literal("Notifications")) {
    companion object {
        const val WIDTH = 140
        const val HEIGHT = 12
    }

    private val notifications = mutableListOf<Notification>()

    fun add(text: MutableComponent, duration: Float = 60f) {
        notifications.add(Notification(text, Timer(duration)))
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (notifications.isEmpty()) return
        val notification = notifications.last()
        val poseStack = guiGraphics.pose()
        poseStack.pushAndPop {
            poseStack.rotateAround(
                Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, -90f)),
                x.toFloat(),
                y.toFloat() + height,
                0f
            )
            guiGraphics.fillGradient(
                x, y + height,
                x - 1 + height, y + width + height,
                FastColor.ARGB32.color(190, 0, 0, 0),
                FastColor.ARGB32.color(0, 0, 0, 0)
            )
        }
        guiGraphics.hLine(
            x + 1,
            x + 1 + (width * (1 - notification.timer.getProgress())).toInt(),
            y,
            FastColor.ARGB32.color(255, 255, 255, 255)
        )
        drawScaledText(
            context = guiGraphics,
            text = notification.text,
            x = x + 2,
            y = y + 5,
            maxCharacterWidth = (width / 0.6f).toInt(),
            scale = 0.6f
//            shadow = true
        )
        notification.timer.tick(f)
        if (notification.timer.isOver()) notifications.remove(notification)
    }

    private data class Notification(val text: MutableComponent, val timer: Timer)
}