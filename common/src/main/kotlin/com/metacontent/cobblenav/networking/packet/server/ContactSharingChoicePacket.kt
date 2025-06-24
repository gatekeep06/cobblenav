package com.metacontent.cobblenav.networking.packet.server

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class ContactSharingChoicePacket(val choice: Boolean) : CobblenavNetworkPacket<ContactSharingChoicePacket> {
    companion object {
        val ID = cobblenavResource("contact_sharing_choice")
        fun decode(buffer: RegistryFriendlyByteBuf) = ContactSharingChoicePacket(buffer.readBoolean())
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeBoolean(choice)
    }
}