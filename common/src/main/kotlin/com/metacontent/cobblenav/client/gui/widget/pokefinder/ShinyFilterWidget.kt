package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.api.generalresources.ColorRepository
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.WIDGET_HEIGHT
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.WIDGET_WIDTH
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.client.gui.widget.button.CheckBox
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.MutableComponent

class ShinyFilterWidget(
    default: Boolean,
    toggle: () -> Unit
) : CheckBox(
    x = 0,
    y = 0,
    width = WIDGET_WIDTH,
    height = WIDGET_HEIGHT,
    texture = TOGGLE,
    default = default,
    afterClick = { toggle() }
) {
    companion object {
        val TOGGLE = gui("pokefinder/shiny_toggle")
    }

    private val text: MutableComponent
        get() = translate(if (checked()) "gui.cobblenav.enabled" else "gui.cobblenav.disabled")
    private val color: Int
        get() = ColorRepository.get(if (checked()) "pokefinder_background" else "pokefinder_text")

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderWidget(guiGraphics, i, j, f)

        drawScaledText(
            context = guiGraphics,
            text = text,
            x = x + width / 2,
            y = y + (height - Minecraft.getInstance().font.lineHeight) / 2 + 1,
            maxCharacterWidth = width,
            centered = true,
            colour = color
        )
    }
}