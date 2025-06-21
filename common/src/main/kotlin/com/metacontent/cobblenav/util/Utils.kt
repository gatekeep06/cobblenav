package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

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

fun RegistryLikeCondition<*>.toResourceLocation(): ResourceLocation? {
    if (this is RegistryLikeIdentifierCondition) {
        return this.identifier
    }
    if (this is RegistryLikeTagCondition) {
        return this.tag.location
    }
    return null
}

fun <T> combinations(vararg lists: Iterable<T>): List<List<T>> {
    return lists.fold(listOf(listOf())) { acc, list ->
        acc.flatMap { combination ->
            list.map { element ->
                combination + element
            }
        }
    }
}