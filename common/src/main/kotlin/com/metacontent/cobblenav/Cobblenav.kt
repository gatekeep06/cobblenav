package com.metacontent.cobblenav

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.metacontent.cobblenav.config.CobblenavConfig
import com.metacontent.cobblenav.networking.packet.client.LabelSyncPacket
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.util.PokenavAreaContextResolver
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Cobblenav {
    const val ID = "cobblenav"
    val LOGGER: Logger = LoggerFactory.getLogger(ID)

    lateinit var config: CobblenavConfig
    lateinit var implementation: Implementation
    val contextResolver = PokenavAreaContextResolver()

    fun init(implementation: Implementation) {
        config = CobblenavConfig.load()
        this.implementation = implementation
        implementation.registerItems()
        registerArgumentTypes()
        implementation.registerCommands()
        implementation.injectLootTables()

        ConditionCollectors.init()

        if (config.syncLabelsWithClient) {
            CobblemonEvents.DATA_SYNCHRONIZED.subscribe { player ->
                LabelSyncPacket(PokemonSpecies.species.map { it.resourceIdentifier to it.labels }).sendToPlayer(player)
            }
        }
    }

    private fun registerArgumentTypes() {
    }
}