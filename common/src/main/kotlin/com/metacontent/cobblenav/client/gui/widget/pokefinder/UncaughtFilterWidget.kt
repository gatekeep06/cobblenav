package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokedex.CaughtPercent
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.api.generalresources.ColorRepository
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderSettingScreen.Companion.FIELD
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderSettingScreen.Companion.LINE_WIDTH
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderSettingScreen.Companion.WIDGET_HEIGHT
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderSettingScreen.Companion.WIDGET_WIDTH
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class UncaughtFilterWidget : SoundlessWidget(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Component.empty()) {
    private val uncaught = 100 - CobblemonClient.clientPokedexData.getGlobalCalculatedValue(CaughtPercent).toInt()

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        blitk(
            matrixStack = guiGraphics.pose(),
            texture = FIELD,
            x = x,
            y = y,
            height = height,
            width = width,
            textureHeight = 2 * height
        )
        drawScaledText(
            context = guiGraphics,
            text = Component.translatable("gui.cobblenav.pokefinder.uncaught", uncaught),
            x = x + 5,
            y = y + 8,
            maxCharacterWidth = LINE_WIDTH,
            colour = ColorRepository.get("pokefinder_text")
        )
    }
}