package com.metacontent.cobblenav.client

import com.metacontent.cobblenav.CobblenavFabricNetworkManager
import com.metacontent.cobblenav.util.cobblenavResource
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.Unit
import net.minecraft.util.profiling.ProfilerFiller
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class CobblenavFabricClient : ClientModInitializer, ClientImplementation {
    override fun onInitializeClient() {
        CobblenavClient.init(this)
        CobblenavFabricNetworkManager.registerClientHandlers()

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(object : IdentifiableResourceReloadListener {
            override fun reload(
                preparationBarrier: PreparableReloadListener.PreparationBarrier,
                resourceManager: ResourceManager,
                profilerFiller: ProfilerFiller,
                profilerFiller2: ProfilerFiller,
                executor: Executor,
                executor2: Executor
            ): CompletableFuture<Void> {
                return preparationBarrier.wait(Unit.INSTANCE).thenRun {
                    CobblenavClient.reloadAssets(resourceManager)
                }
            }

            override fun getFabricId() = cobblenavResource("cobblenav_assets")
        })
    }
}