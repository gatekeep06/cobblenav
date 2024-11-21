package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.util.cobblenavResource
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
        val RADIAL_MENU = cobblenavResource("textures/gui/radialmenu/radial_menu.png")
        val MAP = cobblenavResource("textures/gui/radialmenu/map.png")
        val LOCATION = cobblenavResource("textures/gui/radialmenu/location.png")
        val CONTACTS = cobblenavResource("textures/gui/radialmenu/contacts.png")
        val SWITCH_OFF = cobblenavResource("textures/gui/radialmenu/switch_off.png")
    }

    abstract val blockScreenWidgets: Boolean
}