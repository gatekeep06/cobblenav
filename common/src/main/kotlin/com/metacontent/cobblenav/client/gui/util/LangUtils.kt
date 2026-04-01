package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.api.text.red
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

fun translate(
    key: String,
    placeholder: MutableComponent = Component.literal(key).red()
): MutableComponent {
    val component = Component.translatable(key)
    if (component.string == key) {
        return placeholder
    }
    return component
}

fun translate(location: ResourceLocation, namespace: String): MutableComponent {
    return translate(location.toLanguageKey(namespace), literal(location.path).red())
}

fun tryTranslating(
    key: String,
    placeholder: MutableComponent = Component.literal(key).red()
): Pair<Boolean, MutableComponent> {
    val component = Component.translatable(key)
    if (component.string == key) {
        return false to placeholder
    }
    return true to component
}

fun literal(value: Any): MutableComponent = Component.literal(value.toString())