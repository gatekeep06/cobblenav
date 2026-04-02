package com.metacontent.cobblenav.api.pokefinder

import net.minecraft.resources.ResourceLocation

class BasicDotType(
    override val id: ResourceLocation,
    val texture: ResourceLocation
) : RadarDotType {
    override fun getTexture(): ResourceLocation = texture
}