package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import com.metacontent.cobblenav.client.gui.screen.SpawnDataDisplayer
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.stateful.StatefulWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.joml.Vector3d

class SpawnDataDetailWidget(
    val displayer: SpawnDataDisplayer,
    parentScreen: PokenavScreen,
    x: Int,
    y: Int
) : StatefulWidget(parentScreen, x, y, WIDTH, HEIGHT, Component.literal("Spawn Data Details")) {
    companion object {
        const val WIDTH = PokenavScreen.SCREEN_WIDTH
        const val HEIGHT = PokenavScreen.SCREEN_HEIGHT
        const val MENU_WIDTH = 146
        const val MENU_HEIGHT = 218
        const val CLOSE_WIDTH = 6
        const val CLOSE_HEIGHT = 18
        const val SECTION_WIDTH = 133
        const val SCROLLABLE_HEIGHT = 203
        val MENU = gui("spawndata/menu")
        val CLOSE = gui("spawndata/close")
    }

    val menuX: Int
        get() = x + width - MENU_WIDTH
    val menuY: Int
        get() = y
    val sectionX: Int
        get() = menuX + 9
    val sectionY: Int
        get() = menuY + 7

    val closeButton = IconButton(
        pX = menuX,
        pY = menuY + (height - CLOSE_HEIGHT) / 2,
        pWidth = CLOSE_WIDTH,
        pHeight = CLOSE_HEIGHT,
        action = { displayer.selectedData = null },
        texture = CLOSE
    )

    override var state = initState(ClosedSpawnDataDetail(this, x, y))

    fun renderMenu(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        poseStack.pushAndPop(
            translate = Vector3d(0.0, 0.0, 3000.0)
        ) {
            blitk(
                matrixStack = poseStack,
                texture = MENU,
                x = menuX,
                y = menuY,
                width = MENU_WIDTH,
                height = MENU_HEIGHT
            )
            closeButton.render(guiGraphics, mouseX, mouseY, delta)
        }
    }
}