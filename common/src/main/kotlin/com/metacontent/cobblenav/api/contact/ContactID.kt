package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.api.net.Encodable
import com.metacontent.cobblenav.util.cobblenavResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import java.util.UUID

data class ContactID(
    val uuid: UUID,
    val type: ResourceLocation = cobblenavResource("player")
) : Encodable {
    companion object {
        val CODEC: Codec<ContactID> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                ResourceLocation.CODEC.fieldOf("type").forGetter { it.type }
            ).apply(instance) { uuid, type ->
                ContactID(UUID.fromString(uuid), type)
            }
        }

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
