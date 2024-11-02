package com.metacontent.cobblenav.client

import com.metacontent.cobblenav.CobblenavFabricNetworkManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

class CobblenavFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        CobblenavFabricNetworkManager.registerClientHandlers()
        HudRenderCallback.EVENT.register(CobblenavClient::beforeChatRender)
    }
}