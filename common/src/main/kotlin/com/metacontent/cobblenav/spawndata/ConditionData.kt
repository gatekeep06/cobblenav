package com.metacontent.cobblenav.spawndata

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class ConditionData(
    val condition: String,
    val values: Set<String>
) {
    companion object {
        val CODEC: Codec<ConditionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("condition").forGetter(ConditionData::condition),
                Codec.STRING.listOf().fieldOf("values").forGetter { (_, values) -> values.toList() }
            ).apply(instance) { condition, values -> ConditionData(condition, values.toSet()) }
        }

        val BUFF_CODEC: StreamCodec<RegistryFriendlyByteBuf, ConditionData> = ByteBufCodecs.fromCodecWithRegistries(CODEC)
    }
}