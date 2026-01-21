package com.metacontent.cobblenav.client.gui.widget.button

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import kotlin.math.min

class TextButton(
    pX: Int = 0, pY: Int = 0,
    pWidth: Int = 0, pHeight: Int = 0,
    disabled: Boolean = false,
    action: (PokenavButton) -> Unit,
    var texture: ResourceLocation? = null,
    private val text: MutableComponent,
    private val textureWidth: Int = pWidth,
    private val textureHeight: Int = pHeight,
    private val uOffset: Int = 0,
    private val vOffset: Int = 0,
    private val shadow: Boolean = false
) : PokenavButton(pX, pY, pWidth, pHeight, text, disabled, action) {
    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {

        var argb = 0.9f
        if (disabled) {
            argb -= 0.4f
        }
        else if (isHovered(i, j)) {
            argb += 0.1f
        }

        texture?.let {
            val poseStack = guiGraphics.pose()

            blitk(
                matrixStack = poseStack,
                texture = it,
                x = x,
                y = y,
                width = width,
                height = height,
                textureWidth = textureWidth,
                textureHeight = textureHeight,
                vOffset = vOffset,
                uOffset = uOffset,
                red = argb,
                green = argb,
                blue = argb
            )
        }

        drawScaledText(
            context = guiGraphics,
            text = text,
            x = x + width / 2,
            y = y + (height - Minecraft.getInstance().font.lineHeight) / 2,
            centered = true,
            opacity = min(1f, argb),
            maxCharacterWidth = width,
            shadow = shadow
        )
    }
}
