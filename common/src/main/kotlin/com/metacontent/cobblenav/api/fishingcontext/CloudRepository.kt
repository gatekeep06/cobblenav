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

object CloudRepository : JsonDataRegistry<CloudRepository.CloudList> {
    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    override val id = cobblenavResource("clouds")
    override val observable = SimpleObservable<CloudRepository>()
    override val resourcePath = "fishing_context"
    override val type = PackType.CLIENT_RESOURCES
    override val typeToken: TypeToken<CloudList> = TypeToken.get(CloudList::class.java)

    val clouds = mutableSetOf<ResourceLocation>()

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, CloudList>) {
        clouds.clear()
        data.forEach { (_, list) ->
            if (list.replace == true) clouds.clear()
            clouds.addAll(list.ids)
        }
        observable.emit(this)
        Cobblenav.LOGGER.info("Loaded {} clouds", clouds.size)
    }

    data class CloudList(
        val replace: Boolean?,
        val ids: List<ResourceLocation>
    )
}