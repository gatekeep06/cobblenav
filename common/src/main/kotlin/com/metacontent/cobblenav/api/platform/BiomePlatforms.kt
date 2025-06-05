package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.cobblenavResource
import com.metacontent.cobblenav.util.combinations
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType

object BiomePlatforms : JsonDataRegistry<BiomePlatform> {
    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .registerTypeAdapter(
            TypeToken.getParameterized(HashSet::class.java, PlatformCondition::class.java).type,
            PlatformConditionAdapter
        )
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    override val id = cobblenavResource("biome_platforms")
    override val observable = SimpleObservable<BiomePlatforms>()
    override val resourcePath = "biome_platforms"
    override val type = PackType.SERVER_DATA
    override val typeToken: TypeToken<BiomePlatform> = TypeToken.get(BiomePlatform::class.java)

    private val platforms = hashMapOf<PlatformCondition, BiomePlatform>()

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, BiomePlatform>) {
        platforms.clear()
        data.forEach { (_, platform) ->
            platform.conditions.forEach {
                platforms[it] = platform
            }
        }
        observable.emit(this)
        Cobblenav.LOGGER.info("Loaded {} biome platforms", platforms.size)
    }

    fun firstFitting(context: SpawnDataContext): ResourceLocation? {
        combinations(
            setOf(context.detailId, null),
            context.biomes + null,
            context.structures + null,
            setOf(context.fluid, null)
        ).forEach {
            val platform = platforms[PlatformCondition(
                it[0] as? String?,
                it[1] as? ResourceLocation?,
                it[2] as? ResourceLocation?,
                it[3] as? ResourceLocation?
            )]
            if (platform != null) return platform.id
        }
        return null
    }
}