package com.metacontent.cobblenav.client.gui.widget.button

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.FastColor
import kotlin.math.min

class CheckBox(
    pX: Int, pY: Int,
    pHeight: Int, pWidth: Int,
    disabled: Boolean = false,
    private val text: MutableComponent,
    default: Boolean = false,
    afterClick: (PokenavButton) -> Unit
) : PokenavButton(pX, pY, pWidth, pHeight, text, disabled, {
    (it as CheckBox).checked = !it.checked
    afterClick.invoke(it)
}) {
    companion object {
        const val TEXT_OFFSET: Int = 2
        val COLOR = FastColor.ARGB32.color(255, 240, 240, 240)
        val CHECKMARK = cobblenavResource("textures/gui/button/checked.png")
    }

    var checked = default
    private val font = Minecraft.getInstance().font
    private val scale = min(1f, height.toFloat() / font.lineHeight.toFloat())

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        guiGraphics.renderOutline(x, y, height, height, COLOR)

        if (checked) {
            if (height <= 10) {
                blitk(
                    matrixStack = poseStack,
                    texture = CHECKMARK,
                    x = x,
                    y = y,
                    width = height,
                    height = height
                )
            }
            else {
                guiGraphics.fill(x + 2, y + 2, x + height - 4, y + height - 4, COLOR)
            }
        }

        drawScaledText(
            context = guiGraphics,
            text = text,
            x = x + height + TEXT_OFFSET,
            y = y + 1 * scale,
            scale = scale,
            maxCharacterWidth = width - height - TEXT_OFFSET
        )
    }
}