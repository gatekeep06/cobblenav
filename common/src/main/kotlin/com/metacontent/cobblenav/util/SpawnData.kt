package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.api.spawning.condition.AreaTypeSpawningCondition
import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.registry.BiomeIdentifierCondition
import com.cobblemon.mod.common.registry.BiomeTagCondition
import com.cobblemon.mod.common.registry.BlockIdentifierCondition
import com.cobblemon.mod.common.registry.BlockTagCondition
import com.cobblemon.mod.common.util.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

data class SpawnData(
    val pokemon: RenderablePokemon,
    val spawnChance: Float,
    val encountered: Boolean,
    val biome: ResourceLocation,
    val time: IntRange,
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
            IntRange(buffer.readInt(), buffer.readInt()),
            buffer.readList { it.readString() }.toSet(),
            buffer.readList { it.readResourceLocation() }.toSet(),
            PoseTypeDataSerializer.read(buffer)
        )

        fun collect(
            detail: PokemonSpawnDetail,
            spawnChance: Float,
            contexts: List<AreaSpawningContext>,
            player: ServerPlayer
        ): SpawnData {
            val renderablePokemon = detail.pokemon.asRenderablePokemon()

            val speciesRecord = Cobblemon.playerDataManager.getPokedexData(player)
                .getSpeciesRecord(renderablePokemon.species.resourceIdentifier)
            val encountered = speciesRecord?.hasSeenForm(renderablePokemon.form.name) ?: false

            val contextBiomes = contexts.map { it.biomeName }.toMutableList()
            contexts.map { it.biomeRegistry.getHolder(it.biomeName) }
                .filter { it.isPresent }
                .flatMap { optional -> optional.get().tags().map { it.location }.toList() }
                .let { contextBiomes.addAll(it) }

            val biomeConditions = detail.conditions.flatMap { it.biomes ?: mutableSetOf() }
            val biomes = biomeConditions
                .filterIsInstance<BiomeIdentifierCondition>()
                .map { it.identifier }
                .toMutableList()
            biomeConditions
                .filterIsInstance<BiomeTagCondition>()
                .map { it.tag.location }
                .let { biomes.addAll(it) }
            val biome = biomes.firstOrNull { contextBiomes.contains(it) } ?: cobblemonResource("is_overworld")

            val condition = detail.conditions.firstOrNull { cond ->
                cond.biomes?.any { b ->
                    b is BiomeTagCondition && b.tag.location == biome
                } ?: false
            }

            val additionalConditions = mutableSetOf<String>()
            val neededBlocks = mutableSetOf<ResourceLocation>()
            var time = IntRange(0, 23999)
            condition?.let {
                // TODO: check isThundering
                if (condition.isThundering == true) additionalConditions.add(THUNDER_KEY)
                else if (condition.isRaining == true) additionalConditions.add(RAIN_KEY)
                if (condition.isRaining == false) additionalConditions.add(CLEAR_KEY)
                condition.moonPhase?.ranges?.any { it.contains(player.level().moonPhase) }?.let {
                    if (it) {
                        additionalConditions.add(MoonPhase.ofWorld(player.level()).name.lowercase())
                    }
                }
                condition.timeRange?.ranges?.firstOrNull { it.contains(player.level().dayTime % 23999) }?.let {
                    time = it
                }

//                if (condition is AreaTypeSpawningCondition) {
//                    val contextBlocks = contexts.flatMap { context ->
//                        context.nearbyBlockTypes
//                            .map { context.blockRegistry.getResourceKey(it) }
//                            .filter { it.isPresent }
//                            .map { it.get().location() }
//                    }.toMutableList()
//                    log(contextBlocks.size.toString())
//                    condition.neededNearbyBlocks
//                        ?.filterIsInstance<BlockIdentifierCondition>()
//                        ?.filter { contextBlocks.contains(it.identifier) }
//                        ?.map { it.identifier }
//                        ?.let { neededBlocks.addAll(it) }
//                    log(neededBlocks.size.toString())
////                    condition.neededNearbyBlocks
////                        ?.filterIsInstance<BlockTagCondition>()
////                        ?.map { it.tag.location }
////                        ?.let { neededBlocks.addAll(it) }
//                }
            }

            var pose = PoseType.PROFILE
            if (detail.context.name == SWIMMING_CONTEXT_CONDITION) {
                pose = PoseType.SWIM
            }
            if (biome.path == FLYING_BIOME_CONDITION) {
                pose = PoseType.FLY
            }

            return SpawnData(renderablePokemon, spawnChance, encountered, biome, time, additionalConditions, neededBlocks, pose)
        }
    }

//    var neededBlocksAsItems: Set<ItemStack>? = null
//        get() {
//            if (field == null) {
//                neededBlocks.forEach { log(it.toString()) }
//                val set = mutableSetOf<ItemStack>()
//                neededBlocks.forEach {
//                    BuiltInRegistries.BLOCK.getOptional(it).ifPresent { block ->
//                        val blockItem = block.asItem().defaultInstance
//                        if (set.none { item -> item.tags.toList().containsAll(blockItem.tags.toList()) }) set.add(blockItem)
//                    }
//                }
////                set.forEach { it.tags.forEach { tag -> log(tag.location.toString()) } }
//                field = set
//            }
//            return field
//        }

    fun encode(buffer: RegistryFriendlyByteBuf) {
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
