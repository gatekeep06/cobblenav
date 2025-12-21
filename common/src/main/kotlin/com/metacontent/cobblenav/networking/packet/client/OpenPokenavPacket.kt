package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf

class OpenPokenavPacket(
    val os: PokenavOS,
    val fixedAreaPoint: BlockPos? = null
) : CobblenavNetworkPacket<OpenPokenavPacket> {
    companion object {
        val ID = cobblenavResource("open_pokenav")
        fun decode(buffer: RegistryFriendlyByteBuf) = OpenPokenavPacket(
            os = PokenavOS.decode(buffer),
            fixedAreaPoint = buffer.readNullable { it.readBlockPos() }
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        os.encode(buffer)
        buffer.writeNullable(fixedAreaPoint) { buf, pos -> buf.writeBlockPos(pos) }
    }
}