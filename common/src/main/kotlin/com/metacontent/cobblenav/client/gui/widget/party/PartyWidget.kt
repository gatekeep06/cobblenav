package com.metacontent.cobblenav.client.gui.widget.party

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class PartyWidget(
    playerX: Int, playerY: Int,
) : AbstractWidget(playerX, playerY, 0, 0, Component.literal("Party Widget")
) {
    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
    }

}