package com.metacontent.cobblenav

import com.metacontent.cobblenav.config.CobblenavConfig
import com.metacontent.cobblenav.config.Config
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.networking.packet.client.CloseFishingnavPacket
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.util.PokenavAreaContextResolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Cobblenav {
    const val ID = "cobblenav"
    val LOGGER: Logger = LoggerFactory.getLogger(ID)

    lateinit var config: CobblenavConfig
    lateinit var implementation: Implementation
    val contextResolver = PokenavAreaContextResolver()

    fun init(implementation: Implementation) {
        config = Config.load(CobblenavConfig::class.java)
        this.implementation = implementation
        implementation.registerItems()
        registerArgumentTypes()
        implementation.registerCommands()

        ConditionCollectors.init()

//        CobblenavEvents.FISH_TRAVEL_STARTED.subscribe { event ->
//            CloseFishingnavPacket().sendToPlayer(event.player)
//        }
    }

    private fun registerArgumentTypes() {
    }
}