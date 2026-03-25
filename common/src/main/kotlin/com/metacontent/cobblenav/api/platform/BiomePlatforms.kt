package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.spawning.SpawnLoader
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType

object BiomePlatforms : JsonDataRegistry<BiomePlatform> {
    override val gson: Gson = SpawnLoader.gson
    override val id = cobblenavResource("biome_platforms")
    override val observable = SimpleObservable<BiomePlatforms>()
    override val resourcePath = "biome_platforms"
    override val type = PackType.SERVER_DATA
    override val typeToken: TypeToken<BiomePlatform> = TypeToken.get(BiomePlatform::class.java)

    private val platforms = mutableListOf<BiomePlatform>()

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, BiomePlatform>) {
        platforms.clear()
        platforms.addAll(data.values)
        observable.emit(this)
        Cobblenav.LOGGER.info("Loaded {} biome platforms", platforms.size)
    }

    fun firstFitting(spawnablePositions: List<SpawnablePosition>): ResourceLocation? {
        spawnablePositions.forEach { spawnablePosition ->
            return platforms.firstOrNull { it.fits(spawnablePosition) }?.id
        }
        return null
    }
}