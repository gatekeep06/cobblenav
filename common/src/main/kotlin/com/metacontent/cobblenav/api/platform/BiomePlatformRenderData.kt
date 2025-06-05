package com.metacontent.cobblenav.api.platform

import net.minecraft.resources.ResourceLocation

data class BiomePlatformRenderData(
    val id: ResourceLocation,
    val background: ResourceLocation,
    val foreground: ResourceLocation,
    val selectedBackground: ResourceLocation,
    val selectedForeground: ResourceLocation,
    val selectedPokemonOffset: Int = 0
)
