package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.*
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.MutableComponent

data class SpawnData(
    val pokemon: RenderablePokemon,
    val spawnChance: Float,
    val encountered: Boolean,
    val conditions: List<MutableComponent>,
    val blockConditions: BlockConditions
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf): SpawnData = SpawnData(
            RenderablePokemon.loadFromBuffer(buffer),
            buffer.readFloat(),
            buffer.readBoolean(),
            buffer.readList { (it as RegistryFriendlyByteBuf).readText().copy() },
            BlockConditions.decode(buffer)
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        pokemon.saveToBuffer(buffer)
        buffer.writeFloat(spawnChance)
        buffer.writeBoolean(encountered)
        buffer.writeCollection(conditions) { buf, component -> (buf as RegistryFriendlyByteBuf).writeText(component) }
        blockConditions.encode(buffer)
    }
}
