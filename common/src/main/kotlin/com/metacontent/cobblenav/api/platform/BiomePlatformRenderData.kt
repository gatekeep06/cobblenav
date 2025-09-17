package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.api.gui.blitk
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.resources.ResourceLocation
import org.joml.Vector2i

data class BiomePlatformRenderData(
    val id: ResourceLocation,
    val platform: ResourceLocation? = null,
    val details: ResourceLocation? = null,
    val platformHighlighting: HoverHighlighting? = null,
    val detailsHighlighting: HoverHighlighting? = null,
    val hoveredPokemonOffset: Vector2i? = null
) {
    fun renderPlatform(poseStack: PoseStack, x: Int, y: Int, width: Int, height: Int, hovered: Boolean) {
        val texture = (if (hovered) platformHighlighting?.texture ?: platform else platform) ?: return
        val rgb = 1.0f + if (hovered) platformHighlighting?.tintOffset ?: 0f else 0f
        blitk(
            matrixStack = poseStack,
            texture = texture,
            x = x + if (hovered) platformHighlighting?.offset?.x ?: 0 else 0,
            y = y + if (hovered) platformHighlighting?.offset?.y ?: 0 else 0,
            width = width,
            height = height,
            red = rgb,
            green = rgb,
            blue = rgb
        )
    }

    fun renderDetails(poseStack: PoseStack, x: Int, y: Int, width: Int, height: Int, hovered: Boolean) {
        val texture = (if (hovered) detailsHighlighting?.texture ?: details else details) ?: return
        val rgb = 1.0f + if (hovered) detailsHighlighting?.tintOffset ?: 0f else 0f
        blitk(
            matrixStack = poseStack,
            texture = texture,
            x = x + if (hovered) detailsHighlighting?.offset?.x ?: 0 else 0,
            y = y + if (hovered) detailsHighlighting?.offset?.y ?: 0 else 0,
            width = width,
            height = height,
            red = rgb,
            green = rgb,
            blue = rgb
        )
    }

    fun getPokemonXOffset(hovered: Boolean) = if (hovered) hoveredPokemonOffset?.x ?: 0 else 0

    fun getPokemonYOffset(hovered: Boolean) = if (hovered) hoveredPokemonOffset?.y ?: 0 else 0
}
