package com.metacontent.cobblenav.api.fishingcontext

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType

object CloudRepository : JsonDataRegistry<List<ResourceLocation>> {
    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    override val id = cobblenavResource("biome_platform_textures")
    override val observable = SimpleObservable<CloudRepository>()
    override val resourcePath = "clouds"
    override val type = PackType.CLIENT_RESOURCES

    @Suppress("UNCHECKED_CAST")
    override val typeToken: TypeToken<List<ResourceLocation>> =
        TypeToken.getParameterized(List::class.java, ResourceLocation::class.java) as TypeToken<List<ResourceLocation>>

    val clouds = mutableSetOf<ResourceLocation>()

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, List<ResourceLocation>>) {
        clouds.clear()
        data.forEach { (_, list) ->
            clouds.addAll(list)
        }
        observable.emit(this)
        Cobblenav.LOGGER.info("Loaded {} clouds", clouds.size)
    }
}