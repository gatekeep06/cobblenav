package com.metacontent.cobblenav.api.platform

import net.minecraft.resources.ResourceLocation
import org.joml.Vector2i

data class HoverHighlighting(
    val offset: Vector2i? = null,
    val texture: ResourceLocation? = null,
    val tintOffset: Float? = null
)