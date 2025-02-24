package com.metacontent.cobblenav.client.gui.widget.button

import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import com.metacontent.cobblenav.client.gui.util.renderMultilineTextTooltip
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor

class InfoButton(
    pX: Int,
    pY: Int,
    pWidth: Int,
    pHeight: Int,
    private val header: MutableComponent,
    private val body: MutableComponent,
    texture: ResourceLocation? = TEXTURE,
    textureWidth: Int = pWidth,
    textureHeight: Int = pHeight,
    uOffset: Int = 0,
    vOffset: Int = 0,
    private val parent: PokenavScreen,
    private val headerColor: Int = FastColor.ARGB32.color(255, 173, 232, 244)
) : IconButton(pX, pY, pWidth, pHeight, false, { it.isFocused = !it.isFocused }, texture, header, textureWidth, textureHeight, uOffset, vOffset) {
    companion object {
        const val TARGET_WIDTH: Int = 170
        val TEXTURE = cobblenavResource("textures/gui/button/support_button.png")
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderWidget(guiGraphics, i, j, f)
        if (isFocused) {
            isFocused = isHovered
        }
        if (!isFocused) return
        guiGraphics.renderMultilineTextTooltip(
            header = header,
            body = body,
            targetWidth = TARGET_WIDTH,
            mouseX = i,
            mouseY = j,
            x1 = parent.screenX + PokenavScreen.VERTICAL_BORDER_DEPTH,
            y1 = parent.screenY + PokenavScreen.HORIZONTAL_BORDER_DEPTH,
            x2 = parent.screenX + PokenavScreen.WIDTH - PokenavScreen.VERTICAL_BORDER_DEPTH,
            y2 = parent.screenY + PokenavScreen.HEIGHT - PokenavScreen.HORIZONTAL_BORDER_DEPTH,
            headerColor = headerColor
        )
    }
}