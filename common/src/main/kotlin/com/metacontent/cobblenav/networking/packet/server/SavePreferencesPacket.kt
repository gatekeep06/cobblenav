package com.metacontent.cobblenav.networking.packet.server

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.metacontent.cobblenav.client.gui.util.Sorting
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class SavePreferencesPacket(val bucketIndex: Int, val sorting: Sorting) : NetworkPacket<SavePreferencesPacket> {
    companion object {
        val ID = cobblenavResource("save_preferences")
        fun decode(buffer: RegistryFriendlyByteBuf) = SavePreferencesPacket(
            buffer.readInt(),
            buffer.readEnum(Sorting::class.java)
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeInt(bucketIndex)
        buffer.writeEnum(sorting)
    }
}