package com.metacontent.cobblenav.client.gui.widget

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.cobblenavScissor
import com.metacontent.cobblenav.client.gui.util.drawBlurredArea
import com.metacontent.cobblenav.client.gui.util.splitText
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.button.PokenavButton
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.FastColor

class ContextMenuWidget(
    text: List<MutableComponent>,
    pX: Int,
    pY: Int,
    private val lineHeight: Int = 12,
    private val textWidth: Int = WIDTH - 4,
    private val centerText: Boolean = true,
    acceptAction: ((ContextMenuWidget, PokenavButton) -> Unit)? = null,
    cancelAction: (ContextMenuWidget, PokenavButton) -> Unit
) : SoundlessWidget(pX, pY, WIDTH, 0, Component.literal("Context Menu")) {
    companion object {
        const val WIDTH: Int = 220
        const val TOP_HEIGHT: Int = 9
        const val BOTTOM_HEIGHT: Int = 8
        const val BUTTON_WIDTH: Int = 15
        const val BUTTON_HEIGHT: Int = 16
        const val BUTTON_VERTICAL_OFFSET: Int = 5
        const val BUTTON_HORIZONTAL_OFFSET: Int = 1
        const val BUTTON_SPACE: Int = 3
        const val OPENING: Float = 2f
        val COLOR: Int = FastColor.ARGB32.color(225, 132, 195, 219)
        val MENU_TOP = cobblenavResource("textures/gui/context_menu_top.png")
        val MENU_BOTTOM = cobblenavResource("textures/gui/context_menu_bottom.png")
        val ACCEPT = cobblenavResource("textures/gui/button/accept_button.png")
        val CANCEL = cobblenavResource("textures/gui/button/cancel_button.png")
    }

    private var acceptButton: IconButton? = null
    private val cancelButton: IconButton
    private val scale = lineHeight.toFloat() / Minecraft.getInstance().font.lineHeight.toFloat()
    private val dividedText = text.flatMap { splitText(it, (textWidth / scale).toInt()) }
    val openingTimer = Timer(OPENING)

    init {
        height = TOP_HEIGHT + dividedText.size * lineHeight + BOTTOM_HEIGHT
        y -= height / 2

        acceptAction?.let {
            acceptButton = IconButton(
                pX = x + width - 2 * BUTTON_WIDTH - BUTTON_SPACE + BUTTON_HORIZONTAL_OFFSET,
                pY = y + height - BUTTON_HEIGHT + BUTTON_VERTICAL_OFFSET,
                pWidth = BUTTON_WIDTH,
                pHeight = BUTTON_HEIGHT,
                texture = ACCEPT,
                action = { acceptAction.invoke(this, it) }
            ).also { addWidget(it) }
        }
        cancelButton = IconButton(
            pX = x + width - BUTTON_WIDTH + BUTTON_HORIZONTAL_OFFSET,
            pY = y + height - BUTTON_HEIGHT + BUTTON_VERTICAL_OFFSET,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            texture = CANCEL,
            action = { cancelAction.invoke(this, it) }
        ).also { addWidget(it) }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.cobblenavScissor(
            x, (y + 3 + (height / 2) * (1 - openingTimer.getProgress())).toInt(),
            x + width, (y + height - 3 - (height / 2 - BOTTOM_HEIGHT) * (1 - openingTimer.getProgress())).toInt()
        )
        guiGraphics.drawBlurredArea(
            x1 = x,
            y1 = y + 3,
            x2 = x + width,
            y2 = y + height - 3,
            blur = 1f,
            delta = f
        )
        guiGraphics.fill(x, y + 3, x + width, y + height - 3, COLOR)
        dividedText.forEachIndexed { index, line ->
            drawScaledText(
                context = guiGraphics,
                text = line,
                x = x + (if (centerText) width / 2f else (width - textWidth) / 2f),
                y = y + height / 2f - (dividedText.size / 2f - index) * lineHeight,
                centered = centerText,
//                maxCharacterWidth = (textWidth / scale).toInt(),
                scale = scale
            )
        }
        guiGraphics.disableScissor()
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = MENU_TOP,
            x = x,
            y = y + (height / 2) * (1 - openingTimer.getProgress()),
            width = WIDTH,
            height = TOP_HEIGHT
        )
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = MENU_BOTTOM,
            x = x,
            y = y + height - BOTTOM_HEIGHT - (height / 2 - BOTTOM_HEIGHT) * (1 - openingTimer.getProgress()),
            width = WIDTH,
            height = BOTTOM_HEIGHT
        )

        if (openingTimer.isOver()) {
            acceptButton?.render(guiGraphics, i, j, f)
            cancelButton.render(guiGraphics, i, j, f)
        }

        openingTimer.tick(f)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (!openingTimer.isOver()) return false
        return super.mouseClicked(pMouseX, pMouseY, pButton)
    }
}