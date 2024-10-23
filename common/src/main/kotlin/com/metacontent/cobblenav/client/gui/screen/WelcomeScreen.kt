package com.metacontent.cobblenav.client.gui.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.awt.Color

class WelcomeScreen(
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(makeOpeningSound, animateOpening, Component.literal("Welcome")) {
    override val color = Color.decode("#000000").rgb

    override fun initScreen() {
    }

    override fun renderScreen(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
    }
}