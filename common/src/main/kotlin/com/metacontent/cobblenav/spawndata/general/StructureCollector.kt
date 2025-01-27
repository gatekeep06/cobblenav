package com.metacontent.cobblenav.spawndata.general

import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.mojang.datafixers.util.Either
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.level.levelgen.structure.Structure

class StructureCollector : GeneralConditionCollector() {
    override fun collect(
        condition: SpawningCondition<*>,
        contexts: List<SpawningContext>,
        player: ServerPlayer
    ): MutableComponent? {
        condition.structures?.let { neededStructures ->
            val structures = getInfluencedStructures(neededStructures, contexts)
            return if (structures.siblings.isEmpty()) null else structures
        }
        return null
    }

    private fun getInfluencedStructures(
        neededStructures: MutableList<Either<ResourceLocation, TagKey<Structure>>>,
        contexts: List<SpawningContext>
    ): MutableComponent {
        val structures = Component.translatable("gui.cobblenav.spawn_data.structures")
        contexts.flatMap { context ->
            val structureAccess = context.world.structureManager()
            val cache = context.getStructureCache(context.position)
            neededStructures.filter {
                    str -> str.map({ cache.check(structureAccess, context.position, it) }, { cache.check(structureAccess, context.position, it) })
            }
        }.distinct().forEach {
            it.ifLeft { resource -> structures.append(Component.translatable(resource.toLanguageKey("structure"))) }
            it.ifRight { tag -> structures.append(Component.translatable(tag.location.toLanguageKey("structure"))) }
        }
        return structures
    }
}