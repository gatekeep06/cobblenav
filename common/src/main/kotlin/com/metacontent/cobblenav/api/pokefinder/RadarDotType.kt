package com.metacontent.cobblenav.api.pokefinder

import net.minecraft.resources.ResourceLocation

interface RadarDotType {
    val id: ResourceLocation

    fun getTexture(): ResourceLocation?
}