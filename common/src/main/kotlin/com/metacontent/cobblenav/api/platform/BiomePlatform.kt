package com.metacontent.cobblenav.api.platform

import net.minecraft.resources.ResourceLocation

data class BiomePlatform(
    val id: ResourceLocation,
    val conditions: HashSet<PlatformCondition>,
    val anticonditions: HashSet<PlatformCondition>?
)