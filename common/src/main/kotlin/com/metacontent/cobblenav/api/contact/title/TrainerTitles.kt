package com.metacontent.cobblenav.api.contact.title

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType

object TrainerTitles : JsonDataRegistry<TrainerTitle> {
    override val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .setPrettyPrinting()
        .create()
    override val id = cobblenavResource("titles")
    override val observable = SimpleObservable<PokeRods>()
    override val resourcePath = "titles"
    override val type = PackType.SERVER_DATA
    override val typeToken: TypeToken<TrainerTitle> = TypeToken.get(TrainerTitle::class.java)

    private val titles = mutableMapOf<ResourceLocation, TrainerTitle>()

    fun getTitle(id: ResourceLocation): TrainerTitle? = titles[id]

    fun getAllowed(granted: Set<ResourceLocation>): MutableSet<TrainerTitle> = titles.values.filter {
        it.commonUse || granted.contains(it.id)
    }.toMutableSet()

    override fun sync(player: ServerPlayer) {

    }

    override fun reload(data: Map<ResourceLocation, TrainerTitle>) {
        data.forEach {
            it.value.id = it.key
            titles[it.key] = it.value
        }
    }
}