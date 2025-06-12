package com.metacontent.cobblenav.event

import com.metacontent.cobblenav.spawndata.collector.ClientCollector

interface CustomClientCollectorRegistrar {
    fun register(collector: ClientCollector): CustomClientCollectorRegistrar
}