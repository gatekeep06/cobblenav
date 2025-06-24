package com.metacontent.cobblenav.client.gui.widget.contact

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.PokenavButton
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class ContactSharingSwitch(
    x: Int,
    y: Int,
    default: Boolean,
    disabled: Boolean = false,
    afterClick: (PokenavButton) -> Unit
) : PokenavButton(x, y, SIZE, SIZE, Component.empty(), disabled, {
    (it as ContactSharingSwitch).enabled = !it.enabled
    afterClick(it)
}) {
    companion object {
        const val SIZE = 16
        val ENABLED = gui("contact/contact_sharing_enabled")
        val DISABLED = gui("contact/contact_sharing_disabled")
    }

    var enabled = default

    override fun renderWidget(
        guiGraphics: GuiGraphics,
        i: Int,
        j: Int,
        f: Float
    ) {
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = if (enabled) ENABLED else DISABLED,
            x = x,
            y = y,
            width = width,
            height = height
        )
    }
}