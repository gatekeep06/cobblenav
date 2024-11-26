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
        if (isHovered()) {
            timer.tick(delta)
        }
        else if (timer.getProgress() != 0f) {
            timer.reset()
        }

        blitk(
            guiGraphics.pose(),
            RADIAL_MENU,
            x, y - 2 * timer.getProgress(),
            width = MENU_DIAMETER,
            height = MENU_DIAMETER,
            textureWidth = ANIMATION_SHEET_WIDTH,
            red = if (isHovered()) 1.1 else 1,
            green = if (isHovered()) 1.1 else 1,
            blue = if (isHovered()) 1.1 else 1,
            alpha = if (isHovered()) 1 else 0.6
        )
    }

    override val blockScreenWidgets: Boolean = false

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (clicked(pMouseX, pMouseY) && isValidClickButton(pButton)) {
            handler.changeState(OpeningRadialMenu(handler, x, y))
            return true
        }
        return false
    }
}