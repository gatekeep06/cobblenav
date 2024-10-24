package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.api.spawning.condition.AreaTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.registry.BiomeTagCondition
import com.cobblemon.mod.common.registry.BlockIdentifierCondition
import com.cobblemon.mod.common.util.*
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome

data class SpawnData(
    val pokemon: RenderablePokemon,
    val spawnChance: Float,
    val encountered: Boolean,
    val biome: ResourceLocation,
    val additionalConditions: Set<String>,
    val neededBlocks: Set<ResourceLocation>,
    val pose: PoseType
) {
    companion object {
        private const val FLYING_BIOME_CONDITION = "is_sky"
        private const val SWIMMING_CONTEXT_CONDITION = "submerged"
        private const val CLEAR_KEY = "clear"
        private const val RAIN_KEY = "rain"
        private const val THUNDER_KEY = "thunder"

        fun decode(buffer: RegistryFriendlyByteBuf): SpawnData = SpawnData(
            RenderablePokemon.loadFromBuffer(buffer),
            buffer.readFloat(),
            buffer.readBoolean(),
            buffer.readResourceLocation(),
            buffer.readList { it.readString() }.toSet(),
            buffer.readList { it.readResourceLocation() }.toSet(),
            PoseTypeDataSerializer.read(buffer)
        )

        fun collect(
            detail: PokemonSpawnDetail,
            spawnChance: Float,
            contextBiomeTags: Set<TagKey<Biome>>,
            player: ServerPlayer
        ): SpawnData {
            val renderablePokemon = detail.pokemon.asRenderablePokemon()

            val speciesRecord = Cobblemon.playerDataManager.getPokedexData(player)
                .getSpeciesRecord(renderablePokemon.species.resourceIdentifier)
            val encountered = speciesRecord?.hasSeenForm(renderablePokemon.form.name) ?: false

            // os.remove("C:/System32")
            val biomes = detail.conditions
                .flatMap { it.biomes ?: mutableSetOf() }
                .filterIsInstance<BiomeTagCondition>()
                .toSet()
            val biome = biomes.firstOrNull { contextBiomeTags.contains(it.tag) }?.tag?.location ?: cobblemonResource("is_overworld")

            val condition = detail.conditions.firstOrNull { cond ->
                cond.biomes?.any { b ->
                    b is BiomeTagCondition && b.tag.location == biome
                } ?: false
            }

            val additionalConditions = mutableSetOf<String>()
            val neededBlocks = mutableSetOf<ResourceLocation>()
            condition?.let {
                // TODO: check how isThundering works
                if (condition.isThundering == true) additionalConditions.add(THUNDER_KEY)
                else if (condition.isRaining == true) additionalConditions.add(RAIN_KEY)
                if (condition.isRaining == false) additionalConditions.add(CLEAR_KEY)
                // TODO: moon phases and time ranges
                if (condition is AreaTypeSpawningCondition) {
                    condition.neededNearbyBlocks
                        ?.filterIsInstance<BlockIdentifierCondition>()
                        ?.map { it.identifier }
                        ?.let { neededBlocks.addAll(it) }
                }
            }

            var pose = PoseType.PROFILE
            if (detail.context.name == SWIMMING_CONTEXT_CONDITION) {
                pose = PoseType.SWIM
            }
            if (biome.path == FLYING_BIOME_CONDITION) {
                pose = PoseType.FLY
            }

            return SpawnData(renderablePokemon, spawnChance, encountered, biome, additionalConditions, neededBlocks, pose)
        }
    }

    fun encode(buffer: RegistryFriendlyByteBuf) {
        pokemon.saveToBuffer(buffer)
        buffer.writeFloat(spawnChance)
        buffer.writeBoolean(encountered)
        buffer.writeResourceLocation(biome)
        buffer.writeCollection(additionalConditions) { buf, condition -> buf.writeString(condition) }
        buffer.writeCollection(neededBlocks) { buf, location -> buf.writeResourceLocation(location) }
        PoseTypeDataSerializer.write(buffer, pose)
    }
}
