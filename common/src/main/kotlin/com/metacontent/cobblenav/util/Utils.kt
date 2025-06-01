package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
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

fun RegistryLikeCondition<*>.toResourceLocation(): ResourceLocation? {
    if (this is RegistryLikeIdentifierCondition) {
        return this.identifier
    }
    if (this is RegistryLikeTagCondition) {
        return this.tag.location
    }
    return null
}