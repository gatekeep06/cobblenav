package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.client.CobblemonClient
import com.metacontent.cobblenav.client.gui.widget.StatusBarWidget
import com.metacontent.cobblenav.client.gui.widget.party.PartyMemberWidget
import com.metacontent.cobblenav.client.gui.widget.party.PartyWidget
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialMenuState
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialPopupMenu
import com.metacontent.cobblenav.os.PokenavOS
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import java.awt.Color

class MainScreen(
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Main")) {
    override val color = FastColor.ARGB32.color(255, 79, 189, 201)

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

        PartyWidget(
            playerX = screenX + VERTICAL_BORDER_DEPTH + 80,
            playerY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - 20,
            scale = 18f,
            pokemon = CobblemonClient.storage.myParty.slots
        ).also { addBlockableWidget(it) }
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

    }
}