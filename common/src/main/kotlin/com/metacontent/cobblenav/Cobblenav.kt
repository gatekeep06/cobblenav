package com.metacontent.cobblenav

import com.metacontent.cobblenav.util.PokenavAreaContextResolver
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Cobblenav {
    const val ID = "cobblenav"
    val LOGGER: Logger = LoggerFactory.getLogger(ID)

    lateinit var implementation: Implementation
    val contextResolver = PokenavAreaContextResolver()

    fun init(implementation: Implementation) {
        this.implementation = implementation
        implementation.registerItems()
        registerArgumentTypes()
        implementation.registerCommands()
    }

    private fun registerArgumentTypes() {
    }
}