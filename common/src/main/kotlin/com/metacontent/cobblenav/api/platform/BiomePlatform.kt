package com.metacontent.cobblenav.api.platform

import net.minecraft.resources.ResourceLocation

data class BiomePlatform(
    val biomeIds: Set<ResourceLocation>,
    val background: ResourceLocation? = null,
    val foreground: ResourceLocation? = null,
    val selectedBackground: ResourceLocation? = null,
    val selectedForeground: ResourceLocation? = null,
    val selectedPokemonOffset: Int = 0
)
