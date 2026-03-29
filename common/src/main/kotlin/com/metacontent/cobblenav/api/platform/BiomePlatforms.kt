package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.spawning.MoonPhaseRange
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.cobblemon.mod.common.api.spawning.position.SpawnablePositionType
import com.cobblemon.mod.common.util.adapters.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.cobblenavResource
import com.mojang.datafixers.util.Either
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.material.Fluid

object BiomePlatforms : JsonDataRegistry<BiomePlatform> {
    override val id = cobblenavResource("biome_platforms")
    override val observable = SimpleObservable<BiomePlatforms>()
    override val resourcePath = "biome_platforms"
    override val type = PackType.SERVER_DATA
    override val typeToken: TypeToken<BiomePlatform> = TypeToken.get(BiomePlatform::class.java)

    private val platforms = mutableListOf<BiomePlatform>()
    private var groupedPlatforms = hashMapOf<ResourceLocation, MutableList<BiomePlatform>>()

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, BiomePlatform>) {
        platforms.clear()
        platforms.addAll(data.values)
        observable.emit(this)
        Cobblenav.LOGGER.info("Loaded {} biome platforms", platforms.size)
    }

    fun firstFitting(spawnablePositions: List<SpawnablePosition>): ResourceLocation? {
        spawnablePositions.forEachIndexed { index, spawnablePosition ->
            val biomeId = spawnablePosition.biomeHolder.unwrapKey().map { it.location() }.orElse(null)
            groupedPlatforms[biomeId]?.firstOrNull { it.fits(spawnablePosition) }?.id?.let {
                return it
            }
        }
        return null
    }

    fun onServerStarted(server: MinecraftServer) {
        val biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME)
        groupedPlatforms.clear()

        platforms.forEach { platform ->
            biomeRegistry.holders().forEach { holder ->
                val key = holder.unwrapKey().orElse(null) ?: return@forEach
                if (platform.anticondition?.biomes != null && platform.anticondition.biomes!!.any { it.fits(holder) }) return@forEach
                if (platform.condition.biomes == null || platform.condition.biomes!!.isEmpty() || platform.condition.biomes!!.any { it.fits(holder) }) {
                    groupedPlatforms.getOrPut(key.location()) { mutableListOf() }.add(platform)
                }
            }
        }
    }

    override val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .setLenient()
        .registerTypeAdapter(
            TypeToken.getParameterized(RegistryLikeCondition::class.java, Biome::class.java).type,
            BiomeLikeConditionAdapter
        )
        .registerTypeAdapter(
            TypeToken.getParameterized(RegistryLikeCondition::class.java, Block::class.java).type,
            BlockLikeConditionAdapter
        )
        .registerTypeAdapter(
            TypeToken.getParameterized(RegistryLikeCondition::class.java, Fluid::class.java).type,
            FluidLikeConditionAdapter
        )
        .registerTypeAdapter(
            TypeToken.getParameterized(
                Either::class.java,
                ResourceLocation::class.java,
                TypeToken.getParameterized(
                    TagKey::class.java,
                    Structure::class.java
                ).type
            ).type,
            EitherIdentifierOrTagAdapter(Registries.STRUCTURE)
        )
        .registerTypeAdapter(SpawnablePositionType::class.java, RegisteredSpawnablePositionAdapter)
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .registerTypeAdapter(SpawningCondition::class.java, CobblenavSpawningConditionAdapter)
        .registerTypeAdapter(TimeRange::class.java, IntRangesAdapter(TimeRange.timeRanges) { TimeRange(*it) })
        .registerTypeAdapter(
            MoonPhaseRange::class.java,
            IntRangesAdapter(MoonPhaseRange.moonPhaseRanges) { MoonPhaseRange(*it) })
        .registerTypeAdapter(CompoundTag::class.java, NbtCompoundAdapter)
        .registerTypeAdapter(IntRange::class.java, IntRangeAdapter)
        .create()
}