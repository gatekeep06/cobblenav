package com.metacontent.neoforge.client

import com.metacontent.cobblenav.client.ClientImplementation
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.CobblenavCommands
import net.minecraft.client.Minecraft
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.common.NeoForge
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

object CobblenavNeoForgeClient : ClientImplementation {
    fun init() {
        with(MOD_BUS) {
            addListener(this@CobblenavNeoForgeClient::initialize)
        }
        with(NeoForge.EVENT_BUS) {
            addListener(::onRenderGuiOverlayEvent)
        }
    }

    private fun initialize(event: FMLClientSetupEvent) {
        CobblenavClient.init(this)
    }

    private fun onRenderGuiOverlayEvent(event: RenderGuiLayerEvent.Pre) {
        if (event.name == VanillaGuiLayers.CHAT) {
            CobblenavClient.beforeChatRender(event.guiGraphics, Minecraft.getInstance().timer)
        }
    }
}