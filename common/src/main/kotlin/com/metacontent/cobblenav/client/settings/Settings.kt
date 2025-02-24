package com.metacontent.cobblenav.client.settings

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps

abstract class Settings<T : Settings<T>> {
//    abstract val codec: Codec<Settings<T>>

    abstract val name: String

    @Transient
    var changed = false

//    open fun toJson(): JsonElement = codec.encodeStart(JsonOps.INSTANCE, this).orThrow
}