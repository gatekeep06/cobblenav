package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.joml.Vector3d
import org.joml.Vector3f

class OpeningSpawnDataDetail(
    statefulWidget: SpawnDataDetailWidget,
    x: Int,
    y: Int
) : WidgetState<SpawnDataDetailWidget>(
    statefulWidget,
    x,
    y,
    SpawnDataDetailWidget.WIDTH,
    SpawnDataDetailWidget.HEIGHT,
    Component.literal("Opening Spawn Data Details")
) {
    companion object {
        const val ANIMATION_DURATION = 2f
    }

    override val blockScreenWidgets = true

    private val timer = Timer(ANIMATION_DURATION)

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        timer.tick(f)

        val poseStack = guiGraphics.pose()
        val progress = timer.getProgress()

        poseStack.pushAndPop(
            translate = Vector3d((1.0 - progress) * SpawnDataDetailWidget.MENU_WIDTH, 0.0, 0.0)
        ) {
            statefulWidget.renderMenu(guiGraphics, i, j, f)
        }

        val scale = 5f * progress
        poseStack.pushAndPop(
            scale = Vector3f(scale, scale, scale)
        ) {
            statefulWidget.displayer.selectedData?.result?.drawResult(
                poseStack = poseStack,
                x = (x + ((statefulWidget.width - SpawnDataDetailWidget.MENU_WIDTH) / 2)) / scale,
                y = (y + 20 + SpawnDataDetailWidget.MENU_HEIGHT / 2 * (1 - progress)) / scale,
                z = 1000f / scale,
                delta = f / 10
            )
        }

        if (timer.isOver()) {
            statefulWidget.changeState(OpenedSpawnDataDetail(statefulWidget, x, y))
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return true
    }
}