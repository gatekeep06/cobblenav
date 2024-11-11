package com.metacontent.cobblenav.client

import com.metacontent.cobblenav.CobblenavFabricNetworkManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

class CobblenavFabricClient : ClientModInitializer, ClientImplementation {
    override fun onInitializeClient() {
        CobblenavClient.init(this)
        CobblenavFabricNetworkManager.registerClientHandlers()
        HudRenderCallback.EVENT.register(CobblenavClient::beforeChatRender)
    }
}