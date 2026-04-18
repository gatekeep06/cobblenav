package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.client.gui.widget.button.TextButton
import com.metacontent.cobblenav.client.settings.PokefinderSettings
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

class ModeSelectionPokefinderScreen : PokefinderScreen() {
    companion object {
        val DETAILS = gui("pokefinder/mode_details")
        val MODE = gui("pokefinder/mode")
        private val MODE_X = 46
        private val MODE_Y = 67
    }

    private lateinit var simpleModeButton: TextButton
    private lateinit var advancedModeButton: TextButton

    override fun initScreen() {
        simpleModeButton = TextButton(
            pX = screenX + MODE_X,
            pY = screenY + MODE_Y,
            pWidth = WIDGET_WIDTH,
            pHeight = WIDGET_HEIGHT,
            texture = MODE,
            text = translate("gui.cobblenav.pokefinder.mode.simple"),
            color = color,
            action = {
                CobblenavClient.pokefinderSettings?.mode = PokefinderSettings.Mode.SIMPLE
                Minecraft.getInstance().setScreen(SimplePokefinderScreen())
            }
        ).also { addRenderableWidget(it) }
        advancedModeButton = TextButton(
            pX = screenX + MODE_X,
            pY = screenY + MODE_Y + WIDGET_HEIGHT + 6,
            pWidth = WIDGET_WIDTH,
            pHeight = WIDGET_HEIGHT,
            texture = MODE,
            text = translate("gui.cobblenav.pokefinder.mode.advanced"),
            color = color,
            action = {
                CobblenavClient.pokefinderSettings?.mode = PokefinderSettings.Mode.ADVANCED
                Minecraft.getInstance().setScreen(AdvancedPokefinderScreen())
            }
        ).also { addRenderableWidget(it) }
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderBackground(guiGraphics, i, j, f)

        blitk(
            matrixStack = guiGraphics.pose(),
            texture = DETAILS,
            x = screenX,
            y = screenY,
            width = WIDTH,
            height = HEIGHT
        )
    }
}