package com.metacontent.cobblenav.event

import com.cobblemon.mod.common.api.reactive.EventObservable

object CobblenavEvents {
    val REGISTER_CUSTOM_COLLECTORS = EventObservable<CustomCollectorRegistrar>()

    val FISH_TRAVEL_STARTED = EventObservable<FishTravelStartedEvent>()

    val POKEMON_ENCOUNTERED = EventObservable<PokemonEncounteredEvent>()

    // CLIENT EVENTS

    val REGISTER_CUSTOM_CLIENT_COLLECTORS = EventObservable<CustomClientCollectorRegistrar>()

    val CONDITION_SECTION_WIDGETS_CREATED = EventObservable<SpawnDataWidgetsCreatedEvent>()

    val ANTICONDITION_SECTION_WIDGETS_CREATED = EventObservable<SpawnDataWidgetsCreatedEvent>()

    val SPAWN_DATA_WIDGETS_CREATED = EventObservable<SpawnDataWidgetsCreatedEvent>()
}