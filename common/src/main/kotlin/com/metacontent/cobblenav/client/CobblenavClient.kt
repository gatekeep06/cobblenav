package com.metacontent.cobblenav.client

import com.metacontent.cobblenav.client.gui.overlay.PokefinderOverlay
import com.metacontent.cobblenav.item.Pokefinder
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

object CobblenavClient {
    val pokefinderOverlay: PokefinderOverlay by lazy { PokefinderOverlay() }

    fun beforeChatRender(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val player = Minecraft.getInstance().player
        if (Minecraft.getInstance().screen != null) return
        player?.let {
            if (player.handSlots.any { it.item is Pokefinder }) {
                pokefinderOverlay.render(guiGraphics, deltaTracker)
            }
        }
    }
}