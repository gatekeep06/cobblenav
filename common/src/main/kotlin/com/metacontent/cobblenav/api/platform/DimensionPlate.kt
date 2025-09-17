package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.gui.blitk
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.resources.ResourceLocation

data class DimensionPlate(
    val dimension: ResourceLocation,
    val texture: ResourceLocation? = null,
    val highlighting: HoverHighlighting? = null
) {
    fun render(poseStack: PoseStack, x: Int, y: Int, width: Int, height: Int, hovered: Boolean) {
        val texture = (if (hovered) highlighting?.texture ?: texture else texture) ?: return
        val rgb = 1.0f + if (hovered) highlighting?.tintOffset ?: 0f else 0f
        blitk(
            matrixStack = poseStack,
            texture = texture,
            x = x + if (hovered) highlighting?.offset?.x ?: 0 else 0,
            y = y + if (hovered) highlighting?.offset?.y ?: 0 else 0,
            width = width,
            height = height,
            red = rgb,
            green = rgb,
            blue = rgb
        )
    }
}