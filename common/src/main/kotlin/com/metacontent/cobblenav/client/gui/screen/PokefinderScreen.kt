package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.CobblemonRenderable
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class PokefinderScreen : Screen(Component.literal("Pokefinder Screen")), CobblemonRenderable {
    companion object {
        const val WIDTH: Int = 288
        const val HEIGHT: Int = 192
        val BACKGROUND = cobblenavResource("textures/gui/pokefinder/screen_background")
    }

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()
        renderBackground(guiGraphics, i, j, f)
//        blitk(
//            matrixStack = poseStack,
//            texture = BACKGROUND,
//
//        )
    }

    override fun isPauseScreen(): Boolean = false
}