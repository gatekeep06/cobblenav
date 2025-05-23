package com.metacontent.cobblenav.util

import com.metacontent.cobblenav.Cobblenav
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

fun cobblenavResource(name: String, namespace: String = Cobblenav.ID): ResourceLocation {
    return ResourceLocation.fromNamespaceAndPath(namespace, name)
}

fun log(message: String) {
    Cobblenav.LOGGER.info(message)
}