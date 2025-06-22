package com.metacontent.cobblenav.api.event

import com.cobblemon.mod.common.api.reactive.EventObservable
import com.metacontent.cobblenav.api.event.contact.ContactDataCreated
import com.metacontent.cobblenav.api.event.contact.ContactsAdded
import com.metacontent.cobblenav.api.event.contact.ContactsRemoved
import com.metacontent.cobblenav.api.event.contact.ContactsUpdated
import com.metacontent.cobblenav.api.event.fishing.FishTravelStartedEvent
import com.metacontent.cobblenav.api.event.profile.ProfileDataCreated
import com.metacontent.cobblenav.api.event.profile.TitlesGranted
import com.metacontent.cobblenav.api.event.profile.TitlesRemoved
import com.metacontent.cobblenav.event.CustomClientCollectorRegistrar
import com.metacontent.cobblenav.event.CustomCollectorRegistrar

object CobblenavEvents {
    @JvmStatic
    val FISH_TRAVEL_STARTED = EventObservable<FishTravelStartedEvent>()

    @JvmStatic
    val PROFILE_DATA_CREATED = EventObservable<ProfileDataCreated>()

    @JvmStatic
    val TITLES_GRANTED = EventObservable<TitlesGranted>()

    @JvmStatic
    val TITLES_REMOVED = EventObservable<TitlesRemoved>()

    @JvmStatic
    val CONTACT_DATA_CREATED = EventObservable<ContactDataCreated>()

    @JvmStatic
    val CONTACTS_ADDED = EventObservable<ContactsAdded>()

    @JvmStatic
    val CONTACTS_UPDATED = EventObservable<ContactsUpdated>()

    @JvmStatic
    val CONTACTS_REMOVED = EventObservable<ContactsRemoved>()

    @JvmStatic
    val REGISTER_CUSTOM_COLLECTORS = EventObservable<CustomCollectorRegistrar>()

    // CLIENT EVENTS

    @JvmStatic
    val REGISTER_CUSTOM_CLIENT_COLLECTORS = EventObservable<CustomClientCollectorRegistrar>()
}