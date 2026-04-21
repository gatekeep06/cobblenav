package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.networking.packet.server.RequestCatalogueDataPacket
import com.metacontent.cobblenav.os.PokenavOS
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.awt.Color

class CatalogueScreen(
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Map")) {
    override val color = Color.decode("#000000").rgb

    override fun initScreen() {
        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { changeScreen(MainScreen(os)) }
        ).let { addBlockableWidget(it) }

        val ids = CobblenavClient.spawnDataCatalogue.missingCachedData()
        if (ids.isEmpty()) {
            populateCatalogue()
        } else {
            RequestCatalogueDataPacket(ids).sendToServer()
        }
    }

    fun populateCatalogue() {

    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
    }
}