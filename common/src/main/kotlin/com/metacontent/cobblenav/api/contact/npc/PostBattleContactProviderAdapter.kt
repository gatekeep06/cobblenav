package com.metacontent.cobblenav.api.contact.npc

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.minecraft.resources.ResourceLocation
import java.lang.reflect.Type

object PostBattleContactProviderAdapter : JsonDeserializer<PostBattleContactProvider> {
    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): PostBattleContactProvider {
        return when (json) {
            is JsonPrimitive -> if (json.asBoolean) ImmediateContactProvider() else NoContactProvider()
            else -> {
                val jsonObject = json.asJsonObject
                return when (jsonObject.get("type")?.asString) {
                    "dialogue" -> DialogueContactProvider(
                        context.deserialize(
                            jsonObject.get("dialogue"),
                            ResourceLocation::class.java
                        )
                    )

                    "battlerecord" -> BattleRecordProvider()

                    else -> NoContactProvider()
                }
            }
        }
    }
}