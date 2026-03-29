package com.metacontent.cobblenav.networking.packet.client

import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.spawndata.CheckedSpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class FishingMapPacket(
    val fishingMap: Map<String, List<CheckedSpawnData>>
) : CobblenavNetworkPacket<FishingMapPacket> {
    companion object {
        val ID = cobblenavResource("fishing_map")
        fun decode(buffer: RegistryFriendlyByteBuf) = FishingMapPacket(
            buffer.readMap(
                { buf ->
                    buf.readString()
                },
                { buf ->
                    buf.readList {
                        CheckedSpawnData.decode(it as RegistryFriendlyByteBuf)
                    }
                }
            )
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeMap(
            fishingMap,
            { buf, bucket ->
                buf.writeString(bucket)
            },
            { buf, list ->
                buf.writeCollection(list) { buf1, spawnData ->
                    spawnData.encode(buf1 as RegistryFriendlyByteBuf)
                }
            }
        )
    }
}