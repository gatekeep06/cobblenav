package com.metacontent.cobblenav.api.contact.npc

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken
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
                        ),
                        context.deserialize(
                            jsonObject.get("lossDialogue"),
                            ResourceLocation::class.java
                        )
                    )

                    "randomdialogue" -> RandomDialogueContactProvider(
                        context.deserialize(
                            jsonObject.get("dialogues"),
                            TypeToken.getParameterized(List::class.java, ResourceLocation::class.java).type
                        ),
                        context.deserialize(
                            jsonObject.get("lossDialogues"),
                            TypeToken.getParameterized(List::class.java, ResourceLocation::class.java).type
                        )
                    )

                    "battlerecord" -> BattleRecordProvider()

                    else -> NoContactProvider()
                }
            }
        }
    }
}