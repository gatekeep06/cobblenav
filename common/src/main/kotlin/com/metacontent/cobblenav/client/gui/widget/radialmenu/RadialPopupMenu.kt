package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import com.metacontent.cobblenav.client.gui.widget.stateful.StatefulWidget
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.network.chat.Component

class RadialPopupMenu(
    parentScreen: PokenavScreen,
    pX: Int, pY: Int
) : StatefulWidget(
    parentScreen,
    pX,
    pY,
    RadialMenuState.MENU_DIAMETER,
    RadialMenuState.MENU_DIAMETER,
    Component.literal("Radial Popup Menu")
) {
    val os = parentScreen.os

    override var state = initState(ClosedRadialMenu(parentScreen.os, this, pX, pY))

    override fun changeState(state: WidgetState<*>) {
        super.changeState(state)
        wrap(state)
    }

    private fun wrap(state: WidgetState<*>) {
        x += (width - state.width) / 2
        y += (height - state.height) / 2
        state.x = x
        state.y = y
        width = state.width
        height = state.height
    }
}