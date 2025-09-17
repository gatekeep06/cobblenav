package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import org.joml.Vector2i

object BiomePlatformRenderDataRepository : JsonDataRegistry<BiomePlatformRenderData> {
    val DEFAULT = BiomePlatformRenderData(
        id = cobblenavResource("default"),
        platform = gui("biome_platforms/default"),
        platformHighlighting = HoverHighlighting(Vector2i(0, -2)),
        hoveredPokemonOffset = Vector2i(0, -2)
    )
    val FISHING = BiomePlatformRenderData(
        id = cobblenavResource("fishing")
    )

    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    override val id = cobblenavResource("biome_platform_textures")
    override val observable = SimpleObservable<BiomePlatformRenderDataRepository>()
    override val resourcePath = "biome_platforms"
    override val type = PackType.CLIENT_RESOURCES
    override val typeToken: TypeToken<BiomePlatformRenderData> = TypeToken.get(BiomePlatformRenderData::class.java)

    private val platforms = hashMapOf<ResourceLocation, BiomePlatformRenderData>()

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, BiomePlatformRenderData>) {
        platforms.clear()
        data.forEach { (id, platform) ->
            platforms[platform.id] = platform
        }
        observable.emit(this)
        Cobblenav.LOGGER.info("Loaded {} biome platform render data", platforms.size)
    }

    fun get(id: ResourceLocation?) = platforms[id] ?: DEFAULT
}