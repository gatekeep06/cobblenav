package com.metacontent.cobblenav.client.settings.pokefinder

import com.google.gson.*
import java.lang.reflect.Type

object RadarFilterAdapter : JsonSerializer<RadarFilter>, JsonDeserializer<RadarFilter> {
    override fun serialize(
        src: RadarFilter,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return context.serialize(src).asJsonObject
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): RadarFilter {
        val jsonObject = json.asJsonObject
        val type = jsonObject["type"].asString

        val clazz = RadarFilterRegistry.get(type) ?: error("Unknown filter type: $type")

        return context.deserialize(jsonObject, clazz)
    }
}