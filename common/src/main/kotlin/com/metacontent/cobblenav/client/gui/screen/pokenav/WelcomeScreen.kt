package com.metacontent.cobblenav.client.gui.screen.pokenav

import com.metacontent.cobblenav.os.PokenavOS
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.awt.Color

class WelcomeScreen(
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Welcome")) {
    override val color = Color.decode("#000000").rgb

    override fun initScreen() {
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
    }
}