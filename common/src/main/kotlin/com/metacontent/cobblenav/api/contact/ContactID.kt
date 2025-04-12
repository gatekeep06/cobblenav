package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.api.net.Encodable
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import java.util.UUID

data class ContactID(
    val uuid: UUID,
    val type: ResourceLocation = cobblenavResource("player")
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = ContactID(
            uuid = buffer.readUUID(),
            type = buffer.readResourceLocation()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeUUID(uuid)
        buffer.writeResourceLocation(type)
    }
}
