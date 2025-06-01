package com.metacontent.cobblenav.neoforge.client

import com.metacontent.cobblenav.api.platform.BiomePlatforms
import com.metacontent.cobblenav.client.ClientImplementation
import com.metacontent.cobblenav.client.CobblenavClient
import net.minecraft.util.Unit
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
import net.neoforged.neoforge.common.NeoForge
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.concurrent.CompletableFuture

object CobblenavNeoForgeClient : ClientImplementation {
    fun init() {
        with(MOD_BUS) {
            addListener(this@CobblenavNeoForgeClient::initialize)
            addListener(this@CobblenavNeoForgeClient::onRegisterReloadListener)
        }
        with(NeoForge.EVENT_BUS) {
        }
    }

    private fun initialize(event: FMLClientSetupEvent) {
        CobblenavClient.init(this)
    }

    private fun onRegisterReloadListener(event: RegisterClientReloadListenersEvent) {
        event.registerReloadListener { synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor ->
            return@registerReloadListener synchronizer.wait(Unit.INSTANCE).thenRun {
                BiomePlatforms.reload(manager)
            }
        }
    }
}