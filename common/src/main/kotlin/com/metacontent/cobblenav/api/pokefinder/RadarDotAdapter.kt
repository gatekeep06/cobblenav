package com.metacontent.cobblenav.api.pokefinder

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object RadarDotAdapter : JsonDeserializer<RadarDotType> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): RadarDotType {
        val clazz = if (json.asJsonObject["frames"] != null) {
            AnimatedDotType::class.java
        } else {
            BasicDotType::class.java
        }
        return context.deserialize(json, clazz)
    }
}