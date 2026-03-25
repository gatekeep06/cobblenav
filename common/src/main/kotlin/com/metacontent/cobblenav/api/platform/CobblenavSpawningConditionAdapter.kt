package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.spawning.condition.BasicSpawningCondition
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object CobblenavSpawningConditionAdapter : JsonDeserializer<SpawningCondition<*>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SpawningCondition<*> {
        val jsonObject = json.asJsonObject
        val type = jsonObject["type"]?.asString ?: BasicSpawningCondition.NAME
        val clazz = SpawningCondition.getByName(type)!!
        return context.deserialize(json, clazz)
    }
}