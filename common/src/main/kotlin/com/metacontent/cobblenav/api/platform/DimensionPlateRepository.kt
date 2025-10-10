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

object DimensionPlateRepository : JsonDataRegistry<DimensionPlate> {
    val DEFAULT = DimensionPlate(
        dimension = cobblenavResource("default"),
        texture = gui("dimension_plates/overworld")
    )
    val FISHING = DimensionPlate(
        dimension = cobblenavResource("fishing")
    )

    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    override val id = cobblenavResource("dimension_plates")
    override val observable = SimpleObservable<DimensionPlateRepository>()
    override val resourcePath = "dimension_plates"
    override val type = PackType.CLIENT_RESOURCES
    override val typeToken: TypeToken<DimensionPlate> = TypeToken.get(DimensionPlate::class.java)

    private val plates = hashMapOf<ResourceLocation, DimensionPlate>()

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, DimensionPlate>) {
        plates.clear()
        data.forEach { (_, plate) ->
            plates[plate.dimension] = plate
        }
        observable.emit(this)
        Cobblenav.LOGGER.info("Loaded {} dimension plates", plates.size)
    }

    fun get(dimension: ResourceLocation?) = plates[dimension] ?: DEFAULT
}