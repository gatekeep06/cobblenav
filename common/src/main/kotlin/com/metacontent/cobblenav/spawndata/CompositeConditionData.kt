package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import net.minecraft.network.RegistryFriendlyByteBuf

data class CompositeConditionData(
    val conditions: List<ConditionData>,
    val anticonditions: List<ConditionData>,
    val blockConditions: BlockConditions,
    val blockAnticonditions: BlockConditions
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = CompositeConditionData(
            conditions = buffer.readList { ConditionData.BUFF_CODEC.decode(it as RegistryFriendlyByteBuf) },
            anticonditions = buffer.readList { ConditionData.BUFF_CODEC.decode(it as RegistryFriendlyByteBuf) },
            blockConditions = BlockConditions.decode(buffer),
            blockAnticonditions = BlockConditions.decode(buffer)
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(conditions) { buf, data ->
            ConditionData.BUFF_CODEC.encode(
                buf as RegistryFriendlyByteBuf, data
            )
        }
        buffer.writeCollection(anticonditions) { buf, data ->
            ConditionData.BUFF_CODEC.encode(
                buf as RegistryFriendlyByteBuf, data
            )
        }
        blockConditions.encode(buffer)
        blockAnticonditions.encode(buffer)
    }
}
