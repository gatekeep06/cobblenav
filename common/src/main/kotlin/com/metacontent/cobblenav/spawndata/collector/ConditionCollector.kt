package com.metacontent.cobblenav.spawndata.collector

import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cobblemon.mod.common.api.conditional.RegistryLikeTagCondition
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

interface ConditionCollector<T : SpawningCondition<*>> : Collector<T> {
    fun collect(condition: T, contexts: List<SpawningContext>, player: ServerPlayer): MutableComponent?

    fun formatValueRange(min: Number?, max: Number?, useSpaces: Boolean = false): String? {
        return if (min != null && max != null) {
            if (useSpaces) "$min - $max" else "$min-$max"
        }
        else if (min != null) "≥$min"
        else if (max != null) "≤$max"
        else null
    }
}