package com.metacontent.cobblenav.client.gui.widget.radialmenu

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.gui.util.Timer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class ClosedRadialMenu(
    handler: RadialMenuHandler,
    pX: Int, pY: Int
) : RadialMenuState(handler, pX, pY, MENU_DIAMETER, MENU_DIAMETER, Component.literal("Closed Radial Menu")) {
    companion object {
        const val ANIMATION_DURATION: Float = 0.5f
    }

    private val timer = Timer(ANIMATION_DURATION)

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        var rgb = 1f
        var alpha = 0.6f
        if (ishHovered(mouseX, mouseY)) {
            timer.tick(delta)
            rgb = 1.1f
            alpha = 1f
        } else if (timer.getProgress() != 0f) {
            timer.reset()
        }

        blitk(
            guiGraphics.pose(),
            RADIAL_MENU,
            x, y - 2 * timer.getProgress(),
            width = MENU_DIAMETER,
            height = MENU_DIAMETER,
            textureWidth = ANIMATION_SHEET_WIDTH,
            red = rgb,
            green = rgb,
            blue = rgb,
            alpha = alpha
        )
    }

    override val blockScreenWidgets: Boolean = false

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (clicked(pMouseX, pMouseY) && isValidClickButton(pButton) && !handler.getParentScreen().blockWidgets) {
            handler.changeState(OpeningRadialMenu(handler, x, y))
            return true
        }
        return false
    }
}