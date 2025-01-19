package com.metacontent.cobblenav.client.gui.widget.button

import com.cobblemon.mod.common.client.gui.CobblemonRenderable
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component

abstract class PokenavButton(
    pX: Int,
    pY: Int,
    pWidth: Int,
    pHeight: Int,
    message: Component,
    var disabled: Boolean,
    private val action: (PokenavButton) -> Unit
) : AbstractWidget(pX, pY, pWidth, pHeight, message), CobblemonRenderable {
    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
    }

    override fun playDownSound(soundManager: SoundManager) {
    }

    override fun onClick(d: Double, e: Double) {
        if (disabled) {
            return
        }
        activate()
    }

    fun activate() = action.invoke(this)
}