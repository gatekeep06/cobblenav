package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

interface ConditionCollector<T : SpawningCondition<*>> {
    val conditionClass: Class<out SpawningCondition<*>>

    fun supports(condition: SpawningCondition<*>) = conditionClass.isInstance(condition)

    fun collect(condition: T, contexts: List<SpawningContext>, player: ServerPlayer): MutableComponent?

    fun RegistryLikeCondition<*>.toResourceLocation(): ResourceLocation? {
        if (this is RegistryLikeIdentifierCondition) {
            return this.identifier
        }
        if (this is RegistryLikeTagCondition) {
            return this.tag.location
        }
        return null
    }

    fun formatValueRange(min: Number?, max: Number?, useSpaces: Boolean = false): String? {
        return if (min != null && max != null) {
            if (useSpaces) "$min - $max" else "$min-$max"
        }
        else if (min != null) "≥$min"
        else if (max != null) "≤$max"
        else null
    }
}