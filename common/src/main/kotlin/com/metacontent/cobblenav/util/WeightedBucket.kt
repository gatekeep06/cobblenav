package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import net.minecraft.network.RegistryFriendlyByteBuf

data class WeightedBucket(
    val name: String,
    val chance: Float
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = WeightedBucket(
            buffer.readString(),
            buffer.readFloat()
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(name)
        buffer.writeFloat(chance)
    }
}