package com.metacontent.cobblenav.client.gui.widget.button

import com.cobblemon.mod.common.api.gui.blitk
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class IconButton(
    pX: Int = 0, pY: Int = 0,
    pWidth: Int = 0, pHeight: Int = 0,
    disabled: Boolean = false,
    action: () -> Unit,
    var texture: ResourceLocation?,
    message: Component = Component.empty(),
    private val textureWidth: Int = pWidth,
    private val textureHeight: Int = pHeight,
    private val uOffset: Int = 0,
    private val vOffset: Int = 0,
) : PokenavButton(pX, pY, pWidth, pHeight, message, disabled, action) {
    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        var rgb = 1f
        if (disabled) {
            rgb -= 0.2f
        }
        else if (isHovered()) {
            rgb += 0.1f
        }

        poseStack.pushPose()
        blitk(
            guiGraphics.pose(), texture,
            x, y,
            width = width,
            height = height,
            textureWidth = textureWidth,
            textureHeight = textureHeight,
            uOffset = uOffset,
            vOffset = vOffset,
            red = rgb,
            green = rgb,
            blue = rgb
        )
        poseStack.popPose()
    }
}