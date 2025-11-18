package com.metacontent.cobblenav.spawndata

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class ConditionData(
    val condition: String,
    val values: List<Component>
) {
    companion object {
        val CODEC: Codec<ConditionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("condition").forGetter(ConditionData::condition),
                ComponentSerialization.CODEC.listOf().fieldOf("values").forGetter(ConditionData::values)
            ).apply(instance) { condition, values -> ConditionData(condition, values) }
        }

        val BUFF_CODEC: StreamCodec<RegistryFriendlyByteBuf, ConditionData> = ByteBufCodecs.fromCodecWithRegistries(CODEC)
    }
}