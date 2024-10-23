package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class RadialPopupMenu(
    private val parentScreen: PokenavScreen,
    pX: Int, pY: Int
) : SoundlessWidget(pX, pY, RadialMenuState.MENU_DIAMETER, RadialMenuState.MENU_DIAMETER, Component.literal("Radial Popup Menu")), RadialMenuHandler {
    private var state: RadialMenuState = ClosedRadialMenu(this, pX, pY).also { addWidget(it) }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        state.render(guiGraphics, mouseX, mouseY, delta)
    }

    override fun changeState(state: RadialMenuState) {
        removeWidget(this.state)
        this.state = state
        addWidget(state)
        parentScreen.blockWidgets = state.blockScreenWidgets
        wrap(state)
    }

    private fun wrap(state: RadialMenuState) {
        x += (width - state.width) / 2
        y += (height - state.height) / 2
        state.x = x
        state.y = y
        width = state.width
        height = state.height
    }

    override fun getParentScreen(): PokenavScreen = parentScreen
}