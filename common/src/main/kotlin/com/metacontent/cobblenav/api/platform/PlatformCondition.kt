package com.metacontent.cobblenav.api.platform

import net.minecraft.resources.ResourceLocation

data class PlatformCondition(
    val id: String? = null,
    val biome: ResourceLocation? = null,
    val structure: ResourceLocation? = null,
    val fluid: ResourceLocation? = null
) {
    fun anyMatches(context: BiomePlatformContext) = id == context.detailId
            || fluid == context.fluid
            || context.biomes.contains(biome)
            || context.structures.contains(structure)
}
