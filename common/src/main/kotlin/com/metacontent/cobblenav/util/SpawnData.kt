package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.registry.BiomeTagCondition
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BiomeTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome

data class SpawnData(
    val pokemon: RenderablePokemon,
    val spawnChance: Float,
    val encountered: Boolean,
    val biome: ResourceLocation,
    val pose: PoseType
) {
    companion object {
        private const val FLYING_BIOME_CONDITION = "is_sky"
        private const val SWIMMING_CONTEXT_CONDITION = "submerged"

        fun decode(buffer: RegistryFriendlyByteBuf): SpawnData = SpawnData(
            RenderablePokemon.loadFromBuffer(buffer),
            buffer.readFloat(),
            buffer.readBoolean(),
            buffer.readResourceLocation(),
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

            var pose = PoseType.PROFILE
            if (detail.context.name == SWIMMING_CONTEXT_CONDITION) {
                pose = PoseType.SWIM
            }
            if (biome.path == FLYING_BIOME_CONDITION) {
                pose = PoseType.FLY
            }

            return SpawnData(renderablePokemon, spawnChance, encountered, biome, pose)
        }
    }

    fun encode(buffer: RegistryFriendlyByteBuf) {
        pokemon.saveToBuffer(buffer)
        buffer.writeFloat(spawnChance)
        buffer.writeBoolean(encountered)
        buffer.writeResourceLocation(biome)
        PoseTypeDataSerializer.write(buffer, pose)
    }
}
