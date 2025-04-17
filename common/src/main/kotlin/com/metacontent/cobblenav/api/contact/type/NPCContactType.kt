package com.metacontent.cobblenav.api.contact.type

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

data class NPCContactType(
    var npcClass: ResourceLocation,
    val overrideName: String?,
    val title: ResourceLocation?,
    val isSingleContact: Boolean = false
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = NPCContactType(
            npcClass = buffer.readResourceLocation(),
            overrideName = buffer.readNullable { it.readString() },
            title = buffer.readNullable { it.readResourceLocation() },
            isSingleContact = buffer.readBoolean()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeResourceLocation(npcClass)
        buffer.writeNullable(overrideName) { pb, value -> pb.writeString(value) }
        buffer.writeNullable(title) { pb, value -> pb.writeResourceLocation(value) }
        buffer.writeBoolean(isSingleContact)
    }
}