package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.metacontent.cobblenav.client.gui.screen.ContactsScreen
import com.metacontent.cobblenav.client.gui.screen.LocationScreen
import com.metacontent.cobblenav.client.gui.screen.pokenav.MapScreen
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class OpenedRadialMenu(
    handler: RadialMenuHandler,
    pX: Int, pY: Int
) : RadialMenuState(handler, pX, pY, DIAMETER, DIAMETER, Component.literal("Opened Radial Menu")) {
    companion object {
        const val DIAMETER: Int = 100
    }

    private val closeButton = IconButton(
        pX = x, pY = y - MENU_DIAMETER / 2,
        pWidth = MENU_DIAMETER,
        pHeight = MENU_DIAMETER,
        texture = RADIAL_MENU,
        textureWidth = ANIMATION_SHEET_WIDTH,
        uOffset = ANIMATION_SHEET_WIDTH - MENU_DIAMETER,
        action = { handler.changeState(ClosedRadialMenu(handler, x, y)) }
    ).also { addWidget(it) }

    private val buttons = listOf(
        IconButton(
            pWidth = 16, pHeight = 16,
            action = { handler.getParentScreen().changeScreen(MapScreen(handler.os)) },
            texture = MAP,
            disabled = !handler.os.canUseMap
        ),
        IconButton(
            pWidth = 16, pHeight = 16,
            action = { handler.getParentScreen().changeScreen(LocationScreen(handler.os)) },
            texture = LOCATION,
            disabled = !handler.os.canUseLocation
        ),
        IconButton(
            pWidth = 16, pHeight = 16,
            action = { handler.getParentScreen().changeScreen(ContactsScreen(handler.os)) },
            texture = CONTACTS,
            disabled = !handler.os.canUseContacts
        ),
        IconButton(
            pWidth = 16, pHeight = 16,
            action = { handler.getParentScreen().onClose() },
            texture = SWITCH_OFF
        )
    ).also {
        val iterator = it.iterator()
        var buttonIndex = 0.5
        while (iterator.hasNext()) {
            val button = iterator.next()
            val angle = buttonIndex / it.size.toFloat() * PI - PI / 2
            //plus 1 to make it more symmetrical
            button.x = (x + 1 - 40 * sin(angle)).toInt()
            button.y = (y - MENU_DIAMETER / 2 - 40 * cos(angle)).toInt()
            addWidget(button)
            buttonIndex++
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        closeButton.render(guiGraphics, i, j, f)
        buttons.forEach { it.render(guiGraphics, i, j, f) }
    }

    override val blockScreenWidgets: Boolean = true

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        val clicked = super.mouseClicked(pMouseX, pMouseY, pButton)
        if (!clicked) {
            handler.changeState(ClosedRadialMenu(handler, x, y))
        }
        return clicked
    }
}