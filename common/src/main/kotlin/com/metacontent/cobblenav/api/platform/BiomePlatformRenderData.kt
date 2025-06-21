package com.metacontent.cobblenav.api.platform

import net.minecraft.resources.ResourceLocation

data class BiomePlatformRenderData(
    val id: ResourceLocation,
    val background: ResourceLocation? = null,
    val foreground: ResourceLocation? = null,
    val selectedBackground: ResourceLocation? = null,
    val selectedForeground: ResourceLocation? = null,
    val selectedPokemonOffset: Int = 0
) {
    fun getBackground(selected: Boolean) = if (selected) selectedBackground else background

    fun getForeground(selected: Boolean) = if (selected) selectedForeground else foreground

    fun getOffset(selected: Boolean) = if (selected) selectedPokemonOffset else 0
}
