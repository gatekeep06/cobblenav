package com.metacontent.cobblenav.client.gui.screen.pokenav

import com.metacontent.cobblenav.client.gui.widget.StatusBarWidget
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialMenuState
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialPopupMenu
import com.metacontent.cobblenav.os.PokenavOS
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.awt.Color

class ContactsScreen(
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Contacts")) {
    override val color = Color.decode("#C3BEA6").rgb

    override fun initScreen() {
        RadialPopupMenu(
            this,
            screenX + (WIDTH - RadialMenuState.MENU_DIAMETER) / 2,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - RadialMenuState.MENU_DIAMETER / 2
        ).also { addUnblockableWidget(it) }

        StatusBarWidget(
            screenX + WIDTH - VERTICAL_BORDER_DEPTH - StatusBarWidget.WIDTH - 2,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - StatusBarWidget.HEIGHT
        ).also { addUnblockableWidget(it) }

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { changeScreen(MainScreen(os)) }
        ).let { addBlockableWidget(it) }
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
    }
}