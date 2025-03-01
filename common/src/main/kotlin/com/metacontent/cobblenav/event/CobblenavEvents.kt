package com.metacontent.cobblenav.event

import com.cobblemon.mod.common.api.reactive.EventObservable

object CobblenavEvents {
    val FISH_TRAVEL_STARTED = EventObservable<FishTravelingStartedEvent>()
}