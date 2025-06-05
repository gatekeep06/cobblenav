package com.metacontent.cobblenav.api.platform

import net.minecraft.resources.ResourceLocation

data class PlatformCondition(
    val id: String? = null,
    val biome: ResourceLocation? = null,
    val structure: ResourceLocation? = null,
    val fluid: ResourceLocation? = null
)
