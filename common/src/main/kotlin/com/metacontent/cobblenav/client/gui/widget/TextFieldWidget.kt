package com.metacontent.cobblenav.client.gui.widget

import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.gui.util.fillWithOutline
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.FastColor

class TextFieldWidget(
    var fieldX: Int,
    var fieldY: Int,
    width: Int,
    height: Int,
    default: String = "",
    private val fillColor: Int = FastColor.ARGB32.color(255, 0, 0, 0),
    private val outlineColor: Int = FastColor.ARGB32.color(255, 255, 255, 255),
    private val focusedOutlineColor: Int = outlineColor,
    private val hint: MutableComponent = Component.empty(),
    private val onFinish: (value: String) -> Unit = {},
    private val onUpdate: (value: String) -> Unit = {}
) : EditBox(Minecraft.getInstance().font, fieldX + 4, fieldY + (height - 8) / 2, width, height, hint) {
    init {
        isBordered = false
        setMaxLength(256)
        value = default
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.fillWithOutline(
            x1 = fieldX,
            y1 = fieldY,
            x2 = fieldX + width + 4,
            y2 = fieldY + height,
            fillColor = fillColor,
            outlineColor = if (isFocused) focusedOutlineColor else outlineColor
        )
        if (value.isEmpty() && !isFocused) {
            drawScaledText(
                context = guiGraphics,
                text = hint,
                x = x,
                y = y,
                colour = FastColor.ARGB32.color(120, 30, 30, 30)
            )
        }
        super.renderWidget(guiGraphics, i, j, f)
    }

    override fun setFocused(bl: Boolean) {
        super.setFocused(bl)
        if (!bl) {
            finish()
        }
    }

    override fun keyPressed(i: Int, j: Int, k: Int): Boolean {
        // this doesn't really work with regular screens, as pressing ESC closes the screen instantly without passing it to the widgets for processing
        if (i == InputConstants.KEY_ESCAPE) {
            finish()
        }
        return super.keyPressed(i, j, k)
    }

    override fun insertText(string: String) {
        super.insertText(string)
        update()
    }

    fun finish() {
        onFinish(value)
    }

    fun update() {
        onUpdate(value)
    }

    override fun clicked(d: Double, e: Double): Boolean {
        return this.active && this.visible && (d >= x.toDouble() - 4) && (e >= y.toDouble() - (height - 8) / 2) && (d < (this.x + this.getWidth()).toDouble()) && (e < (this.y + this.getHeight() + (height - 8) / 2).toDouble())
    }

    override fun setX(i: Int) {
        super.setX(i + 4)
        fieldX = i
    }

    override fun setY(i: Int) {
        super.setY(i + (height - 8) / 2)
        fieldY = i
    }
}