package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.util.gui
import net.minecraft.network.chat.Component

abstract class RadialMenuState(
    protected val handler: RadialMenuHandler,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    component: Component
) : SoundlessWidget(pX, pY, pWidth, pHeight, component) {
    companion object {
        const val MENU_DIAMETER: Int = 20
        const val ANIMATION_SHEET_WIDTH: Int = 180
        val RADIAL_MENU = gui("radialmenu/radial_menu")
        val MAP = gui("radialmenu/map")
        val LOCATION = gui("radialmenu/location")
        val CONTACTS = gui("radialmenu/contacts")
        val SWITCH_OFF = gui("radialmenu/switch_off")
    }

    abstract val blockScreenWidgets: Boolean
}