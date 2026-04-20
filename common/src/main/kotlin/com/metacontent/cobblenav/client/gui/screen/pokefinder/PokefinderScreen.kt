package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.api.generalresources.ColorRepository
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import org.joml.Vector3f

abstract class PokefinderScreen : Screen(Component.literal("Pokefinder")) {
    companion object {
        const val WIDTH = 288
        const val HEIGHT = 192
        const val BORDER_WIDTH = 5
        const val BUTTON_SIZE = 18
        const val WIDGET_WIDTH = 196
        const val WIDGET_HEIGHT = 26
        const val LINE_WIDTH = 185
        const val LINE_HEIGHT = 26
        val BACKGROUND = gui("pokefinder/background")
        val BACK = gui("pokefinder/back")
        val CLEAR = gui("pokefinder/clear")
        val FIELD = gui("pokefinder/text")
    }

    protected lateinit var backButton: IconButton

    val player: LocalPlayer? = Minecraft.getInstance().player

    val color
        get() = ColorRepository.get("pokefinder_text")
    val bgColor
        get() = ColorRepository.get("pokefinder_background")

    init {
        player?.playSound(CobblemonSounds.PC_ON, 0.1f, 2f)
    }

    val scale = CobblenavClient.config.pokefinderScreenScale

    var screenX = 0
    var screenY = 0

    override fun init() {
        width = (width / scale).toInt()
        height = (height / scale).toInt()

        screenX = (width - WIDTH) / 2
        screenY = (height - HEIGHT) / 2

        backButton = IconButton(
            pX = screenX + BORDER_WIDTH + 1,
            pY = screenY + HEIGHT - BORDER_WIDTH - 1 - BUTTON_SIZE,
            pWidth = BUTTON_SIZE,
            pHeight = BUTTON_SIZE,
            action = { onClose() },
            texture = BACK
        ).also { addRenderableWidget(it) }

        initScreen()
    }

    open fun initScreen() {}

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.pose().pushAndPop(
            scale = Vector3f(scale, scale, 1f)
        ) {
            val scaledMouseX = (i / scale).toInt()
            val scaledMouseY = (j / scale).toInt()
            super.render(guiGraphics, scaledMouseX, scaledMouseY, f)
        }
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderBackground(guiGraphics, i, j, f)

        blitk(
            matrixStack = guiGraphics.pose(),
            texture = BACKGROUND,
            x = screenX,
            y = screenY,
            width = WIDTH,
            height = HEIGHT
        )
    }

    override fun isPauseScreen(): Boolean = false

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
        return super.mouseClicked(d / scale, e / scale, i)
    }

    override fun mouseScrolled(d: Double, e: Double, f: Double, g: Double): Boolean {
        return super.mouseScrolled(d / scale, e / scale, f / scale, g / scale)
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return super.mouseDragged(d / scale, e / scale, i, f / scale, g / scale)
    }

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean {
        return super.mouseReleased(d / scale, e / scale, i)
    }
}