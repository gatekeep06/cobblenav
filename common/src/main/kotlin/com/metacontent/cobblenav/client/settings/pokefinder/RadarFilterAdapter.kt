package com.metacontent.cobblenav.client.settings.pokefinder

import com.google.gson.*
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
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
        val typeName = jsonObject["type"].asString

        val type = RadarFilterTypeRegistry.get(typeName) ?: error("Unknown filter type: $typeName")

        return context.deserialize(jsonObject, type.filterClass)
    }
}