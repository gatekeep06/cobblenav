package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.awt.Color

class MapScreen(
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(makeOpeningSound, animateOpening, Component.literal("Map")) {
    override val color = Color.decode("#000000").rgb

    override fun initScreen() {
        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { changeScreen(MainScreen()) }
        ).let { addBlockableWidget(it) }
    }

    override fun renderScreen(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
    }
}