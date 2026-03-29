package com.metacontent.cobblenav.api.platform

import net.minecraft.resources.ResourceLocation

data class BiomePlatformContext(
    val detailId: String,
    val biomes: Set<ResourceLocation>,
    val structures: Set<ResourceLocation>,
    val fluid: ResourceLocation?
) {
    class Builder {
        var detailId: String = ""
        var biomes = emptySet<ResourceLocation>()
        var structures = emptySet<ResourceLocation>()
        var fluid: ResourceLocation? = null

        fun build() = BiomePlatformContext(detailId, biomes, structures, fluid)
    }
}
