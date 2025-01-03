package com.metacontent.cobblenav.os

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import net.minecraft.network.RegistryFriendlyByteBuf

class PokenavOS(
    val version: String,
    val canUseLocation: Boolean = false,
    val canUseContacts: Boolean = false,
    val canUseMap: Boolean = false
) : Encodable {
    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(version)
        buffer.writeBoolean(canUseLocation)
        buffer.writeBoolean(canUseContacts)
        buffer.writeBoolean(canUseMap)
    }

    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = PokenavOS(
            version = buffer.readString(),
            canUseLocation = buffer.readBoolean(),
            canUseContacts = buffer.readBoolean(),
            canUseMap = buffer.readBoolean()
        )
    }
}