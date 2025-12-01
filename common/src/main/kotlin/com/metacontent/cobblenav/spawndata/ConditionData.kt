package com.metacontent.cobblenav.spawndata

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class ConditionData(
    val condition: String,
    val color: Int,
    val values: List<Component>
) {
    companion object {
        val CODEC: Codec<ConditionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("condition").forGetter(ConditionData::condition),
                Codec.INT.fieldOf("color").forGetter(ConditionData::color),
                ComponentSerialization.CODEC.listOf().fieldOf("values").forGetter(ConditionData::values)
            ).apply(instance) { condition, color, values -> ConditionData(condition, color, values) }
        }

        val BUFF_CODEC: StreamCodec<RegistryFriendlyByteBuf, ConditionData> = ByteBufCodecs.fromCodecWithRegistries(CODEC)
    }

    fun toLine(): MutableComponent {
        val line = Component.translatable("gui.cobblenav.spawn_data.$condition")
        values.forEachIndexed { index, component ->
            line.append(component)
            if (index < values.size - 1) line.append(", ")
        }
        return line.withColor(color)
    }
}