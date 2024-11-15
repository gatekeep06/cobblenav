package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.*
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation

data class SpawnData(
    val pokemon: RenderablePokemon,
    val spawnChance: Float,
    val encountered: Boolean,
    val biome: ResourceLocation,
    val time: IntRange,
    val additionalConditions: Set<String>,
    val neededBlocks: Set<ResourceLocation>,
    val pose: PoseType
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf): SpawnData = SpawnData(
            RenderablePokemon.loadFromBuffer(buffer),
            buffer.readFloat(),
            buffer.readBoolean(),
            buffer.readResourceLocation(),
            IntRange(buffer.readInt(), buffer.readInt()),
            buffer.readList { it.readString() }.toSet(),
            buffer.readList { it.readResourceLocation() }.toSet(),
            PoseTypeDataSerializer.read(buffer)
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        pokemon.saveToBuffer(buffer)
        buffer.writeFloat(spawnChance)
        buffer.writeBoolean(encountered)
        buffer.writeResourceLocation(biome)
        buffer.writeInt(time.first)
        buffer.writeInt(time.last)
        buffer.writeCollection(additionalConditions) { buf, condition -> buf.writeString(condition) }
        buffer.writeCollection(neededBlocks) { buf, location -> buf.writeResourceLocation(location) }
        PoseTypeDataSerializer.write(buffer, pose)
    }
}
