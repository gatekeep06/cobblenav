package com.metacontent.cobblenav.client

import com.metacontent.cobblenav.CobblenavFabricNetworkManager
import net.fabricmc.api.ClientModInitializer

class CobblenavFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        CobblenavFabricNetworkManager.registerClientHandlers()
    }
}