package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialMenuState
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialPopupMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import java.awt.Color

class MainScreen(
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(makeOpeningSound, animateOpening, Component.literal("Main")) {
    override val color = FastColor.ARGB32.color(255, 79, 189, 201)

    override fun initScreen() {

    }

    override fun renderScreen(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

    }
}