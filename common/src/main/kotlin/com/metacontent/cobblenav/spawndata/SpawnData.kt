package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.*
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.MutableComponent

data class SpawnData(
    val renderable: RenderablePokemon,
    val spawnAspects: Set<String>,
    val spawnChance: Float,
    val spawningContext: String,
    val knowledge: PokedexEntryProgress,
    val conditions: MutableList<MutableComponent>,
    val blockConditions: BlockConditions
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf): SpawnData = SpawnData(
            RenderablePokemon.loadFromBuffer(buffer),
            buffer.readList { it.readString() }.toSet(),
            buffer.readFloat(),
            buffer.readString(),
            buffer.readEnum(PokedexEntryProgress::class.java),
            buffer.readList { (it as RegistryFriendlyByteBuf).readText().copy() },
            BlockConditions.decode(buffer)
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        renderable.saveToBuffer(buffer)
        buffer.writeCollection(spawnAspects) { buf, aspect -> buf.writeString(aspect) }
        buffer.writeFloat(spawnChance)
        buffer.writeString(spawningContext)
        buffer.writeEnum(knowledge)
        buffer.writeCollection(conditions) { buf, component -> (buf as RegistryFriendlyByteBuf).writeText(component) }
        blockConditions.encode(buffer)
    }

    fun known() = knowledge != PokedexEntryProgress.NONE
}
