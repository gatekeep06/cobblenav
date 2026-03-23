package com.metacontent.cobblenav.client.gui.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class TextFieldWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    lineWidth: Int = width,
    lineHeight: Int = height,
    lineX: Int = x + (width - lineWidth) / 2,
    lineY: Int = y + (lineHeight - Minecraft.getInstance().font.lineHeight) / 2 + 1,
    default: String = "",
    textColor: Int = 0xffffff,
    private val textureSheet: ResourceLocation,
    private val onChange: (String) -> Unit
) : SoundlessWidget(x, y, width, height, Component.empty()) {
    private val editBox: EditBox = EditBox(
        Minecraft.getInstance().font,
        lineX,
        lineY,
        lineWidth,
        lineHeight,
        message
    ).also {
        it.setMaxLength(Int.MAX_VALUE)
        it.value = default
        it.setTextColor(textColor)
        it.setResponder(onChange)
        it.isBordered = false
        addWidget(it)
    }

    val value: String
        get() = editBox.value

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

    override fun setX(i: Int) {
        editBox.x += i - x
        super.setX(i)
    }

    override fun setY(i: Int) {
        editBox.y += i - y
        super.setY(i)
    }

    override fun setFocused(bl: Boolean) {
        super.setFocused(bl)
        editBox.isFocused = bl
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return clicked(pMouseX, pMouseY).also {
            isFocused = it
            if (it) {
                editBox.onClick(pMouseX, pMouseY)
            }
        }
    }
}