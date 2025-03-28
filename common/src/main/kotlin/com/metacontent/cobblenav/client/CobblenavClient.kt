package com.metacontent.cobblenav.client

import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.metacontent.cobblenav.config.ClientCobblenavConfig
import com.metacontent.cobblenav.client.gui.overlay.PokefinderOverlay
import com.metacontent.cobblenav.client.gui.overlay.TrackArrowOverlay
import com.metacontent.cobblenav.client.settings.ClientSettingsDataManager
import com.metacontent.cobblenav.client.settings.PokefinderSettings
import com.metacontent.cobblenav.item.Pokefinder
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

object CobblenavClient {
    lateinit var implementation: ClientImplementation
    lateinit var config: ClientCobblenavConfig
    private val settingsManager = ClientSettingsDataManager
    var pokefinderSettings: PokefinderSettings? = null
    val pokefinderOverlay: PokefinderOverlay by lazy {
        val overlay = PokefinderOverlay()
        overlay.initialize()
        overlay
    }
    val trackArrowOverlay: TrackArrowOverlay by lazy { TrackArrowOverlay() }

    fun init(implementation: ClientImplementation) {
        config = ClientCobblenavConfig.load()
        this.implementation = implementation
        PlatformEvents.CLIENT_PLAYER_LOGIN.subscribe {
            pokefinderSettings = settingsManager.load(PokefinderSettings.NAME, PokefinderSettings::class.java) as PokefinderSettings
        }
        PlatformEvents.CLIENT_PLAYER_LOGOUT.subscribe {
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
}