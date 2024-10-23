package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.metacontent.cobblenav.client.gui.screen.PokenavScreen

interface RadialMenuHandler {
    fun changeState(state: RadialMenuState)

    fun getParentScreen(): PokenavScreen
}