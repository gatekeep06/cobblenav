package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.readText
import com.cobblemon.mod.common.util.writeString
import com.cobblemon.mod.common.util.writeText
import com.metacontent.cobblenav.util.getHeadBlock
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.GrowingPlantBlock

abstract class SpawnData<T : RenderData>(
    val id: String,
    val renderable: T,
    val positionType: String,
    val spawnChance: Float,
    val platformId: ResourceLocation?,
    val conditions: MutableList<ConditionData>,
    val blockConditions: BlockConditions
) {
    abstract val type: String
}

class PokemonSpawnData(
    id: String,
    renderable: PokemonRenderData,
    positionType: String,
    spawnChance: Float,
    platformId: ResourceLocation?,
    conditions: MutableList<ConditionData>,
    blockConditions: BlockConditions,
    val levelRange: IntRange?
) : SpawnData<PokemonRenderData>(id, renderable, positionType, spawnChance, platformId, conditions, blockConditions) {
    companion object {
        val TYPE = "pokemon"
    }

    override val type = TYPE
}

class PokemonHerdSpawnData(
    id: String,
    renderable: PokemonHerdRenderData,
    positionType: String,
    spawnChance: Float,
    platformId: ResourceLocation?,
    conditions: MutableList<ConditionData>,
    blockConditions: BlockConditions,
    val heardablePokemon: List<PokemonProperties>,
    val levelRange: IntRange,
    val maxHerdSize: Int
) : SpawnData<PokemonHerdRenderData>(
    id,
    renderable, positionType, spawnChance, platformId, conditions, blockConditions
) {
    companion object {
        val TYPE = "pokemon_herd"
    }

    override val type = TYPE
}

data class SpawnData1(
    val renderable: RenderablePokemon,
    val spawnAspects: Set<String>,
    val spawnChance: Float,
    val platform: ResourceLocation?,
    val spawningContext: String,
    val knowledge: PokedexEntryProgress,
    val conditions: MutableList<MutableComponent>,
    val blockConditions: BlockConditions
) : Encodable {
    val encountered = knowledge != PokedexEntryProgress.NONE

    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf): SpawnData1 = SpawnData1(
            RenderablePokemon.loadFromBuffer(buffer),
            buffer.readList { it.readString() }.toSet(),
            buffer.readFloat(),
            buffer.readNullable { it.readResourceLocation() },
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
        buffer.writeNullable(platform) { buf, id -> buf.writeResourceLocation(id) }
        buffer.writeString(spawningContext)
        buffer.writeEnum(knowledge)
        buffer.writeCollection(conditions) { buf, component -> (buf as RegistryFriendlyByteBuf).writeText(component) }
        blockConditions.encode(buffer)
    }
}
