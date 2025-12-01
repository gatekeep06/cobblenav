package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.stateful.StatefulWidget
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import com.metacontent.cobblenav.os.PokenavOS
import net.minecraft.network.chat.Component

abstract class RadialMenuState(
    val os: PokenavOS,
    handler: StatefulWidget,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    component: Component
) : WidgetState<StatefulWidget>(handler, pX, pY, pWidth, pHeight, component) {
    companion object {
        const val MENU_DIAMETER: Int = 20
        const val ANIMATION_SHEET_WIDTH: Int = 180
        val RADIAL_MENU = gui("radialmenu/radial_menu")
        val MAP = gui("radialmenu/map")
        val LOCATION = gui("radialmenu/location")
        val CONTACTS = gui("radialmenu/contacts")
        val SWITCH_OFF = gui("radialmenu/switch_off")
    }
}