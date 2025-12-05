package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class ClosedSpawnDataDetail(
    statefulWidget: SpawnDataDetailWidget,
    x: Int,
    y: Int
) : WidgetState<SpawnDataDetailWidget>(
    statefulWidget,
    x,
    y,
    SpawnDataDetailWidget.WIDTH,
    SpawnDataDetailWidget.HEIGHT,
    Component.literal("Closed Spawn Data Details")
) {
    override val blockScreenWidgets = false

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (statefulWidget.displayer.isDataSelected()) {
            statefulWidget.changeState(OpeningSpawnDataDetail(statefulWidget, x, y))
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return false
    }
}