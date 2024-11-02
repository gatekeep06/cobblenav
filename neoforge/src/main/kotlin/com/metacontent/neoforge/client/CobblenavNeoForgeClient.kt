package com.metacontent.neoforge.client

import com.metacontent.cobblenav.client.CobblenavClient
import net.minecraft.client.Minecraft
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.common.NeoForge

object CobblenavNeoForgeClient {
    fun init() {
        with(NeoForge.EVENT_BUS) {
            addListener(::onRenderGuiOverlayEvent)
        }
    }

    private fun onRenderGuiOverlayEvent(event: RenderGuiLayerEvent.Pre) {
        if (event.name == VanillaGuiLayers.CHAT) {
            CobblenavClient.beforeChatRender(event.guiGraphics, Minecraft.getInstance().timer)
        }
    }
}