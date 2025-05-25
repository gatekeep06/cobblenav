package com.metacontent.cobblenav

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.metacontent.cobblenav.config.CobblenavConfig
import com.metacontent.cobblenav.config.Config
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.networking.packet.client.CloseFishingnavPacket
import com.metacontent.cobblenav.networking.packet.client.LabelSyncPacket
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.util.PokenavSpawningProspector
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Cobblenav {
    const val ID = "cobblenav"
    val LOGGER: Logger = LoggerFactory.getLogger(ID)

    lateinit var config: CobblenavConfig
    lateinit var implementation: Implementation
    val prospector = PokenavSpawningProspector

    fun init(implementation: Implementation) {
        config = Config.load(CobblenavConfig::class.java)
        this.implementation = implementation
        implementation.registerItems()
        registerArgumentTypes()
        implementation.registerCommands()
        implementation.injectLootTables()

        CobblenavEvents.FISH_TRAVEL_STARTED.subscribe { event ->
            CloseFishingnavPacket().sendToPlayer(event.player)
        }
      
        if (config.syncLabelsWithClient) {
            CobblemonEvents.DATA_SYNCHRONIZED.subscribe { player ->
                LabelSyncPacket(PokemonSpecies.species.map { it.resourceIdentifier to it.labels }).sendToPlayer(player)
            }
        }
    }

    fun onCobblemonInit() {
        ConditionCollectors.init()
    }

    private fun registerArgumentTypes() {
    }
}