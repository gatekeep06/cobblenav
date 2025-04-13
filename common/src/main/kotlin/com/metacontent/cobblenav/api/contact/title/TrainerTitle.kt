package com.metacontent.cobblenav.api.contact.title

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.*
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

data class TrainerTitle(
    var id: ResourceLocation,
    val overrideName: String?,
    val overrideBannerId: ResourceLocation?,
    val commonUse: Boolean
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = TrainerTitle(
            id = buffer.readResourceLocation(),
            overrideName = buffer.readNullable { it.readString() },
            overrideBannerId = buffer.readNullable { it.readResourceLocation() },
            commonUse = buffer.readBoolean()
        )
    }

    fun name(): MutableComponent = overrideName?.let { Component.translatable(it) } ?: Component.translatable(id.toLanguageKey("title"))

    fun banner(): ResourceLocation = overrideBannerId ?: ResourceLocation.fromNamespaceAndPath(id.namespace, "textures/gui/banner/${id.path}")

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeIdentifier(id)
        buffer.writeNullable(overrideName) { pb, value -> pb.writeString(value) }
        buffer.writeNullable(overrideBannerId) { pb, value -> pb.writeResourceLocation(value) }
        buffer.writeBoolean(commonUse)
    }
}