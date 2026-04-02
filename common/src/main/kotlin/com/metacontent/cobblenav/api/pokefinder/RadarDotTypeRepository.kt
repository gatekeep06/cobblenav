package com.metacontent.cobblenav.api.pokefinder

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType

object RadarDotTypeRepository : JsonDataRegistry<RadarDotType> {
    val DEFAULT = BasicDotType(
        id = cobblenavResource("default"),
        texture = gui("pokefinder/dot")
    )

    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .registerTypeAdapter(RadarDotType::class.java, RadarDotAdapter)
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    override val id = cobblenavResource("radar_dots")
    override val observable = SimpleObservable<RadarDotTypeRepository>()
    override val resourcePath = "radar_dots"
    override val type = PackType.CLIENT_RESOURCES
    override val typeToken: TypeToken<RadarDotType> = TypeToken.get(RadarDotType::class.java)

    private val dots = hashMapOf<ResourceLocation, RadarDotType>()

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, RadarDotType>) {
        dots.clear()
        dots.putAll(data.values.associateBy { it.id })
        observable.emit(this)
        Cobblenav.LOGGER.info("Loaded {} radar dot types", dots.size)
        CobblenavClient.pokefinderSettings?.getFilters()?.forEach {
            it.dotType = get(it.dotType?.id)
        }
    }

    fun get(id: ResourceLocation?): RadarDotType = id?.let { dots[it] } ?: DEFAULT
}