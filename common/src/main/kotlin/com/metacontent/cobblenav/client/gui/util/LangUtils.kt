package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.api.text.red
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

fun translate(
    key: String,
    placeholder: MutableComponent = Component.literal(key).red()
): Pair<Boolean, MutableComponent> {
    val component = Component.translatable(key)
    if (component.string == key) {
        return Pair(false, placeholder)
    }
    return Pair(true, component)
}

fun literal(value: String): MutableComponent = Component.literal(value)