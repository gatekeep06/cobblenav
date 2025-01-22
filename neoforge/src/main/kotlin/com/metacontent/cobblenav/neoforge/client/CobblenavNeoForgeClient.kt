package com.metacontent.cobblenav.neoforge.client

import com.metacontent.cobblenav.client.ClientImplementation
import com.metacontent.cobblenav.client.CobblenavClient
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.common.NeoForge
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

object CobblenavNeoForgeClient : ClientImplementation {
    fun init() {
        with(MOD_BUS) {
            addListener(this@CobblenavNeoForgeClient::initialize)
        }
        with(NeoForge.EVENT_BUS) {
        }
    }

    private fun initialize(event: FMLClientSetupEvent) {
        CobblenavClient.init(this)
    }
}