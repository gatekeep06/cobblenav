package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.spawning.condition.*
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.registry.BlockIdentifierCondition
import com.cobblemon.mod.common.registry.BlockTagCondition
import com.cobblemon.mod.common.util.cobblemonResource
import com.metacontent.cobblenav.client.gui.util.getTimeString
import com.mojang.datafixers.util.Either
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.structure.Structure
import kotlin.jvm.optionals.getOrNull

object SpawnDataHelper {
    const val BIOME_KEY_BASE = "biome"
    const val STRUCTURE_KEY_BASE = "structure"
    const val WEATHER_KEY_BASE = "weather.cobblenav"
    const val FLUID_KEY_BASE = "tag.fluid"
    const val MOON_KEY_BASE = "moon.cobblenav"
    private const val FLYING_BIOME_CONDITION = "is_sky"
    private const val SWIMMING_CONTEXT_CONDITION = "submerged"
    private const val CLEAR_KEY = "clear"
    private const val RAIN_KEY = "rain"
    private const val THUNDER_KEY = "thunder"
    private const val SLIME_CHUNK_KEY = "slime_chunk"
    private const val MAX_TIME = 23999

    fun collect(
        detail: PokemonSpawnDetail,
        spawnChance: Float,
        contexts: List<AreaSpawningContext>,
        player: ServerPlayer
    ): SpawnData {
        val renderablePokemon = detail.pokemon.let {
            val pokemon = Pokemon()
            it.apply(pokemon)
            pokemon.asRenderablePokemon()
        }

        val aspects = detail.pokemon.aspects

        val condition = detail.conditions.firstOrNull { contexts.any { context -> it.isSatisfiedBy(context) } }
        val fittingContexts = contexts.filter { condition?.isSatisfiedBy(it) == true }

        val speciesRecord = Cobblemon.playerDataManager.getPokedexData(player)
            .getSpeciesRecord(renderablePokemon.species.resourceIdentifier)
        val encountered = speciesRecord?.hasSeenForm(renderablePokemon.form.name) ?: false

        val conditions = mutableListOf<MutableComponent>()
        val blocks = mutableSetOf<ResourceLocation>()
        condition?.let {
            if (it is GroundedTypeSpawningCondition<*>) {
                blocks += getInfluencedBlocks(it, fittingContexts)
            }
            if (it is SubmergedTypeSpawningCondition<*>) {
                conditions += collectConditions(it, fittingContexts, player)
            }
            if (it is SeafloorTypeSpawningCondition<*>) {
                blocks += getInfluencedBlocks(it, fittingContexts)
            }
            if (it is SurfaceTypeSpawningCondition<*>) {
                conditions += collectConditions(it, fittingContexts, player)
            }
            if (it is AreaTypeSpawningCondition<*>) {
                blocks += getInfluencedBlocks(it, fittingContexts)
            }
            conditions += collectConditions(it, fittingContexts, player)
        }

        return SpawnData(renderablePokemon, aspects, spawnChance, encountered, conditions, BlockConditions(blocks))
    }

    private fun collectConditions(
        condition: SpawningCondition<*>,
        fittingContexts: List<AreaSpawningContext>,
        player: ServerPlayer
    ): MutableList<MutableComponent> {
        val conditions = mutableListOf<MutableComponent>()

        conditions.add(getInfluencedBiomes(condition, fittingContexts))

        condition.structures?.let { neededStructures ->
            val structures = getInfluencedStructures(neededStructures, fittingContexts)
            if (structures.siblings.isNotEmpty()) conditions.add(structures)
        }

        condition.timeRange?.let { time ->
            val range = time.ranges.firstOrNull { it.contains(player.level().dayTime % MAX_TIME) }
            if (range != null) {
                conditions.add(Component.translatable("gui.cobblenav.spawn_data.time", getTimeString(range)))
            }
        }

        val weather = Component.translatable("gui.cobblenav.spawn_data.weather")
        if (condition.isThundering == true) weather.append(Component.translatable("$WEATHER_KEY_BASE.$THUNDER_KEY"))
        if (condition.isRaining == true) weather.append(Component.translatable("$WEATHER_KEY_BASE.$RAIN_KEY"))
        if (condition.isRaining == false) weather.append(Component.translatable("$WEATHER_KEY_BASE.$CLEAR_KEY"))
        if (weather.siblings.isNotEmpty()) conditions.add(weather)

        val height = getValueRangeString(condition.minY, condition.maxY, true)
        if (height != null) conditions.add(Component.translatable("gui.cobblenav.spawn_data.height", height))

        val coordinates = Component.empty()
        getValueRangeString(condition.minX, condition.maxX)?.let {
            coordinates.append(Component.translatable("gui.cobblenav.spawn_data.coordinates.x", "$it "))
        }
        getValueRangeString(condition.minZ, condition.maxZ)?.let {
            coordinates.append(Component.translatable("gui.cobblenav.spawn_data.coordinates.z", it))
        }
        if (coordinates.siblings.isNotEmpty()) conditions.add(coordinates)

        if (condition.moonPhase != null) {
            conditions.add(
                Component.translatable("gui.cobblenav.spawn_data.moon")
                    .append(Component.translatable("$MOON_KEY_BASE.${MoonPhase.ofWorld(player.level()).name.lowercase()}"))
            )
        }

        condition.canSeeSky?.let {
            conditions.add(Component.translatable("gui.cobblenav.spawn_data.can_see_sky")
                .append(Component.translatable("gui.cobblenav.$it")))
        }

        val light = getValueRangeString(condition.minLight, condition.maxLight)
        if (light != null) conditions.add(Component.translatable("gui.cobblenav.spawn_data.light", light))

        val skyLight = getValueRangeString(condition.minSkyLight, condition.maxSkyLight)
        if (skyLight != null) conditions.add(Component.translatable("gui.cobblenav.spawn_data.light", skyLight))

        condition.isSlimeChunk?.let {
            conditions.add(Component.translatable("gui.cobblenav.spawn_data.slime_chunk")
                .append(Component.translatable("gui.cobblenav.$it")))
        }

        return conditions
    }

    private fun collectConditions(
        condition: SurfaceTypeSpawningCondition<*>,
        fittingContexts: List<AreaSpawningContext>,
        player: ServerPlayer
    ): MutableList<MutableComponent> {
        val conditions = mutableListOf<MutableComponent>()
        condition.fluid?.toResourceLocation()?.let {
            conditions.add(Component.translatable("gui.cobblenav.spawn_data.fluid")
                .append(Component.translatable("$FLUID_KEY_BASE.c.${it.path}")))
        }
        return conditions
    }

    private fun collectConditions(
        condition: SubmergedTypeSpawningCondition<*>,
        fittingContexts: List<AreaSpawningContext>,
        player: ServerPlayer
    ): MutableList<MutableComponent> {
        val conditions = mutableListOf<MutableComponent>()
        condition.fluid?.toResourceLocation()?.let {
            conditions.add(Component.translatable("gui.cobblenav.spawn_data.fluid")
                .append(Component.translatable("$FLUID_KEY_BASE.c.${it.path}")))
        }
        return conditions
    }

    private fun getValueRangeString(min: Number?, max: Number?, useSpaces: Boolean = false): String? {
        return if (min != null && max != null) {
            if (useSpaces) "$min - $max" else "$min-$max"
        }
        else if (min != null) "≥$min"
        else if (max != null) "≤$max"
        else null
    }

    private fun getInfluencedBiomes(condition: SpawningCondition<*>, contexts: List<AreaSpawningContext>): MutableComponent {
        val biomes = Component.translatable("gui.cobblenav.spawn_data.habitat")
        condition.biomes
            ?.mapNotNull { it.toResourceLocation() }
            ?.filter {
                contexts.any { context ->
                    val registry = context.biomeRegistry
                    val biomeLocation = registry.getKey(context.biome) ?: return@any false
                    val biomeTags = registry.getHolder(biomeLocation).getOrNull()
                        ?.tags()?.map { it.location }?.toList() ?: return@any false
                    return@any biomeLocation == it || biomeTags.contains(it)
                }
            }
            ?.distinct()
            ?.forEach { biomes.append(Component.translatable(it.toLanguageKey(BIOME_KEY_BASE))) }
        if (biomes.siblings.isEmpty()) biomes.append(cobblemonResource("is_overworld").toLanguageKey(BIOME_KEY_BASE))
        return biomes
    }

    private fun getInfluencedStructures(
        neededStructures: MutableList<Either<ResourceLocation, TagKey<Structure>>>,
        contexts: List<AreaSpawningContext>
    ): MutableComponent {
        val structures = Component.translatable("gui.cobblenav.spawn_data.structures")
        contexts.flatMap { context ->
            val structureAccess = context.world.structureManager()
            val cache = context.getStructureCache(context.position)
            neededStructures.filter {
                    str -> str.map({ cache.check(structureAccess, context.position, it) }, { cache.check(structureAccess, context.position, it) })
            }
        }.distinct().forEach {
            it.ifLeft { resource -> structures.append(Component.translatable(resource.toLanguageKey(STRUCTURE_KEY_BASE))) }
            it.ifRight { tag -> structures.append(Component.translatable(tag.location.toLanguageKey(STRUCTURE_KEY_BASE))) }
        }
        return structures
    }

    private fun getInfluencedBlocks(
        condition: AreaTypeSpawningCondition<*>,
        contexts: List<AreaSpawningContext>
    ): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededNearbyBlocks?.toBlockList() ?: emptyList()
        contexts.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }

    private fun getInfluencedBlocks(
        condition: GroundedTypeSpawningCondition<*>,
        contexts: List<AreaSpawningContext>
    ): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededBaseBlocks?.toBlockList() ?: emptyList()
        contexts.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }

    private fun getInfluencedBlocks(
        condition: SeafloorTypeSpawningCondition<*>,
        contexts: List<AreaSpawningContext>
    ): Set<ResourceLocation> {
        val blocks = mutableSetOf<ResourceLocation>()
        val neededBlocks = condition.neededBaseBlocks?.toBlockList() ?: emptyList()
        contexts.flatMap { it.nearbyBlockTypes }.forEach {
            val block = BuiltInRegistries.BLOCK.getKey(it)
            if (neededBlocks.contains(block)) {
                blocks.add(block)
            }
        }
        return blocks
    }

    private fun RegistryLikeCondition<*>.toResourceLocation(): ResourceLocation? {
        if (this is RegistryLikeIdentifierCondition) {
            return this.identifier
        }
        if (this is RegistryLikeTagCondition) {
            return this.tag.location
        }
        return null
    }

    private fun MutableList<RegistryLikeCondition<Block>>.toBlockList(): List<ResourceLocation> {
        return this.flatMap {
            if (it is BlockIdentifierCondition) {
                return@flatMap listOf(it.identifier)
            }
            if (it is BlockTagCondition) {
                if (it.tag.location.path == "natural") return@flatMap emptyList()
                val optional = BuiltInRegistries.BLOCK.getTag(it.tag)
                if (optional.isPresent) {
                    return@flatMap optional.get().map { blockHolder -> BuiltInRegistries.BLOCK.getKey(blockHolder.value()) }
                }
            }
            return@flatMap emptyList()
        }
    }
}