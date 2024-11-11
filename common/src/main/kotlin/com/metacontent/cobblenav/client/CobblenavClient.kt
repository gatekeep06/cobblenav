package com.metacontent.cobblenav.client

import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.metacontent.cobblenav.client.gui.overlay.PokefinderOverlay
import com.metacontent.cobblenav.item.Pokefinder
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

object CobblenavClient {
    lateinit var implementation: ClientImplementation
    val pokefinderOverlay: PokefinderOverlay by lazy {
        val overlay = PokefinderOverlay()
        overlay.initialize()
        overlay
    }

    fun init(implementation: ClientImplementation) {
        this.implementation = implementation
        PlatformEvents.CLIENT_PLAYER_LOGOUT.subscribe {
            if (pokefinderOverlay.settings?.changed == true) {
                pokefinderOverlay.settings?.save()
            }
        }
    }

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