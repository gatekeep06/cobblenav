package com.metacontent.cobblenav.util

import com.metacontent.cobblenav.Cobblenav
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

fun cobblenavResource(name: String, namespace: String = Cobblenav.ID): ResourceLocation {
    return ResourceLocation.fromNamespaceAndPath(namespace, name)
}

fun log(message: String) {
    Cobblenav.LOGGER.info(message)
}

fun List<Component>.join(base: MutableComponent = Component.empty(), separator: CharSequence = ", "): MutableComponent {
    this.forEachIndexed { index, title ->
        base.append(title)
        if (index < this.size - 1) {
            base.append(", ")
        }
    }
    return base
}