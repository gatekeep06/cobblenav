package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.location.BucketSelectorWidget
import com.metacontent.cobblenav.networking.packet.server.RequestCatalogueDataPacket
import com.metacontent.cobblenav.os.PokenavOS
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class CatalogueScreen(
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Map")) {
    companion object {
        const val VIEW_WIDTH = 298
        const val VIEW_HEIGHT = 182

        val VIEW = gui("catalogue/view_bg")
    }

    var viewX = 0
    var viewY = 0
    override val color = FastColor.ARGB32.color(255, 58, 150, 182)

    override fun initScreen() {
        viewX = screenX + VERTICAL_BORDER_DEPTH + 5
        viewY = screenY + HORIZONTAL_BORDER_DEPTH + 20

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
        val poseStack = guiGraphics.pose()
        blitk(
            matrixStack = poseStack,
            texture = VIEW,
            x = viewX,
            y = viewY - BucketSelectorWidget.HEIGHT,
            width = VIEW_WIDTH,
            height = VIEW_HEIGHT + BucketSelectorWidget.HEIGHT
        )
    }
}