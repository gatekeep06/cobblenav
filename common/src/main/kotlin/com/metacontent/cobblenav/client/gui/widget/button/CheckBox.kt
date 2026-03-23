package com.metacontent.cobblenav.client.gui.widget.button

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.util.gui
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor
import kotlin.math.min

class CheckBox(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    disabled: Boolean = false,
    private val texture: ResourceLocation,
    private val textOffset: Int = 2,
    private val text: MutableComponent? = null,
    default: Boolean = false,
    afterClick: (CheckBox) -> Unit
) : PokenavButton(x, y, width, height, Component.empty(), disabled, {
    (it as CheckBox).checked = !it.checked
    afterClick.invoke(it)
}) {
    companion object {
        val CHECK_BOX = gui("button/default_checkbox")
    }

    private var checked = default

    private val font = Minecraft.getInstance().font
    private val scale = min(1f, height.toFloat() / font.lineHeight.toFloat())

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        blitk(
            matrixStack = poseStack,
            texture = texture,
            x = x,
            y = y,
            width = width,
            height = height,
            textureHeight = 2 * height,
            vOffset = if (checked) height else 0
        )

        text?.let {
            drawScaledText(
                context = guiGraphics,
                text = it,
                x = x + width + textOffset,
                y = y + 1 * scale,
                scale = scale
            )
        }
    }

    fun checked() = checked
}