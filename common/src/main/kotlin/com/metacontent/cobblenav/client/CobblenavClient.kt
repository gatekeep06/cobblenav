package com.metacontent.cobblenav.client

import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.metacontent.cobblenav.api.fishingcontext.CloudRepository
import com.metacontent.cobblenav.api.platform.BiomePlatformRenderDataRepository
import com.metacontent.cobblenav.api.platform.DimensionPlateRepository
import com.metacontent.cobblenav.client.gui.overlay.PokefinderOverlay
import com.metacontent.cobblenav.client.gui.overlay.TrackArrowOverlay
import com.metacontent.cobblenav.client.settings.ClientSettingsDataManager
import com.metacontent.cobblenav.client.settings.PokefinderSettings
import com.metacontent.cobblenav.client.settings.PokenavSettings
import com.metacontent.cobblenav.config.ClientCobblenavConfig
import com.metacontent.cobblenav.config.Config
import com.metacontent.cobblenav.item.Pokefinder
import com.metacontent.cobblenav.spawndata.collector.ClientCollectors
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.server.packs.resources.ResourceManager

object CobblenavClient {
    lateinit var implementation: ClientImplementation
    lateinit var config: ClientCobblenavConfig
    private val settingsManager = ClientSettingsDataManager
    var pokenavSettings: PokenavSettings? = null
    var pokefinderSettings: PokefinderSettings? = null
    val pokefinderOverlay: PokefinderOverlay by lazy {
        val overlay = PokefinderOverlay()
        overlay.initialize()
        overlay
    }
    val trackArrowOverlay: TrackArrowOverlay by lazy { TrackArrowOverlay() }

    fun init(implementation: ClientImplementation) {
        config = Config.load(ClientCobblenavConfig::class.java)
        this.implementation = implementation
        PlatformEvents.CLIENT_PLAYER_LOGIN.subscribe {
            pokenavSettings =
                settingsManager.load(PokenavSettings.NAME, PokenavSettings::class.java) as PokenavSettings
            pokefinderSettings =
                settingsManager.load(PokefinderSettings.NAME, PokefinderSettings::class.java) as PokefinderSettings
            ClientCollectors.init()
        }
        PlatformEvents.CLIENT_PLAYER_LOGOUT.subscribe {
            if (pokenavSettings?.changed == true) {
                settingsManager.save(pokenavSettings!!)
            }
            if (pokefinderSettings?.changed == true) {
                settingsManager.save(pokefinderSettings!!)
            }
        }
    }

    fun renderOverlay(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val player = Minecraft.getInstance().player
        if (Minecraft.getInstance().screen != null) return
        player?.let {
            if (player.handSlots.any { it.item is Pokefinder }) {
                pokefinderOverlay.render(guiGraphics, deltaTracker)
            }
            trackArrowOverlay.render(guiGraphics, deltaTracker)
        }
    }

    fun reloadAssets(resourceManager: ResourceManager) {
        BiomePlatformRenderDataRepository.reload(resourceManager)
        DimensionPlateRepository.reload(resourceManager)
        CloudRepository.reload(resourceManager)
    }
}