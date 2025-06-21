package com.metacontent.cobblenav.event

import com.cobblemon.mod.common.api.reactive.EventObservable

object CobblenavEvents {
    val REGISTER_CUSTOM_COLLECTORS = EventObservable<CustomCollectorRegistrar>()

    val FISH_TRAVEL_STARTED = EventObservable<FishTravelStartedEvent>()

    // CLIENT EVENTS

    val REGISTER_CUSTOM_CLIENT_COLLECTORS = EventObservable<CustomClientCollectorRegistrar>()
}