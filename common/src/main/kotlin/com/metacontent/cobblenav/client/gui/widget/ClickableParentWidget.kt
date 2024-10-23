package com.metacontent.cobblenav.client.gui.widget

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import net.minecraft.network.chat.Component

abstract class ClickableParentWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight:
    Int, component: Component
) : SoundlessWidget(pX, pY, pWidth, pHeight, component) {
    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (isHovered()) {
            onClick(pMouseX, pMouseY)
            super.mouseClicked(pMouseX, pMouseY, pButton)
        }
        return isHovered()
    }
}