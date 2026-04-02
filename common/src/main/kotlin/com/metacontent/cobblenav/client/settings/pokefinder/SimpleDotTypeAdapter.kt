package com.metacontent.cobblenav.client.settings.pokefinder

import com.cobblemon.mod.common.util.asResource
import com.google.gson.*
import com.metacontent.cobblenav.api.pokefinder.RadarDotType
import com.metacontent.cobblenav.api.pokefinder.RadarDotTypeRepository
import java.lang.reflect.Type

object SimpleDotTypeAdapter : JsonSerializer<RadarDotType>, JsonDeserializer<RadarDotType> {
    override fun serialize(
        src: RadarDotType,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return context.serialize(src.id)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): RadarDotType {
        return RadarDotTypeRepository.get(json.asString.asResource())
    }
}