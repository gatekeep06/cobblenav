package com.metacontent.cobblenav.client.gui.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class TextFieldWidget(
    val x: Int,
    val y: Int,
    width: Int,
    height: Int,
    lineWidth: Int = width,
    default: String = "",
    textColor: Int = 0xffffff,
    private val textureSheet: ResourceLocation,
    private val onChange: (String) -> Unit
) : SoundlessWidget(x, y, width, height, Component.empty()) {
    private val editBox: EditBox

    val value: String
        get() = editBox.value

    init {
        val font = Minecraft.getInstance().font
        editBox = EditBox(
            font,
            x + (width - lineWidth) / 2,
            y + (height - font.lineHeight) / 2,
            width,
            height,
            message
        ).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.value = default
            it.setTextColor(textColor)
            it.setResponder(onChange)
            addWidget(it)
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = textureSheet,
            x = x,
            y = y,
            width = width,
            height = height,
            vOffset = if (isFocused) height else 0,
            textureHeight = height * 2
        )
        editBox.renderWidget(guiGraphics, i, j, f)
    }
}