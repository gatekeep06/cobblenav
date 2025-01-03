package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import com.metacontent.cobblenav.os.PokenavOS

interface RadialMenuHandler {
    val os: PokenavOS

    fun changeState(state: RadialMenuState)

    fun getParentScreen(): PokenavScreen
}