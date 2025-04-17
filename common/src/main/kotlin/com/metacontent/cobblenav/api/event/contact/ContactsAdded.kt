package com.metacontent.cobblenav.api.event.contact

import com.metacontent.cobblenav.api.contact.PokenavContact
import net.minecraft.server.level.ServerPlayer

data class ContactsAdded(
    val player: ServerPlayer?,
    val contacts: List<PokenavContact>
)
