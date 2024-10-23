package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.google.common.collect.Lists
import com.metacontent.cobblenav.util.cobblenavResource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor

abstract class PokenavScreen(makeOpeningSound: Boolean, animateOpening: Boolean, component: Component) : Screen(component) {
    companion object {
        const val WIDTH: Int = 350
        const val HEIGHT: Int = 250
        const val VERTICAL_BORDER_DEPTH: Int = 21
        const val HORIZONTAL_BORDER_DEPTH: Int = 16
        const val SCREEN_WIDTH = WIDTH - 2 * VERTICAL_BORDER_DEPTH
        const val SCREEN_HEIGHT = HEIGHT - 2 * HORIZONTAL_BORDER_DEPTH
        const val ANIMATION_SPEED: Float = 20f
        const val ANIMATION_OFFSET: Float = 20f
        const val BACK_BUTTON_SIZE: Int = 14
        val DETAILS = cobblenavResource("textures/gui/pokenav_details.png")
        val SCREEN_BASE = cobblenavResource("textures/gui/pokenav_screen_base.png")
        val BORDERS = cobblenavResource("textures/gui/pokenav_borders.png")
        val SCREEN = cobblenavResource("textures/gui/pokenav_screen.png")
        val BACK_BUTTON = cobblenavResource("textures/gui/button/back.png")
    }

    var screenX = 0
    var screenY = 0
    abstract val color: Int
    val player: LocalPlayer? = Minecraft.getInstance().player
    private var animationOffset: Float = if (animateOpening) ANIMATION_OFFSET else 0f
    var blockWidgets: Boolean = false
    private val blockable = Lists.newArrayList<AbstractWidget>()
    private val unblockable = Lists.newArrayList<AbstractWidget>()
    var previousScreen: PokenavScreen? = null

    init {
        if (makeOpeningSound) {
            player?.playSound(CobblemonSounds.PC_ON, 0.1f, 1.25f)
        }
    }

    override fun init() {
        blockWidgets = false
        blockable.clear()
        unblockable.clear()
        screenX = (width - WIDTH) / 2
        screenY = (height - HEIGHT) / 2

        initScreen()
    }

    abstract fun initScreen()

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        renderBackground(guiGraphics, mouseX, mouseY, delta)
        poseStack.pushPose()
        poseStack.translate(0f, animationOffset, 0f)
        renderBaseElement(poseStack, BORDERS)
        renderScreenBackground(guiGraphics, SCREEN, color)
        guiGraphics.enableScissor(
            screenX + VERTICAL_BORDER_DEPTH,
            screenY + HORIZONTAL_BORDER_DEPTH,
            screenX + VERTICAL_BORDER_DEPTH + SCREEN_WIDTH,
            screenY + HORIZONTAL_BORDER_DEPTH + SCREEN_HEIGHT,
        )
        //render blockable widgets and the current screen's stuff
        renderOnBackLayer(guiGraphics, mouseX, mouseY, delta)
        renderWidgets(blockable, guiGraphics, mouseX, mouseY, delta)
        renderOnFrontLayer(guiGraphics, mouseX, mouseY, delta)
        // if true block widgets and screen
        poseStack.pushPose()
        poseStack.translate(0f, 0f, 500f)
        if (blockWidgets) {
            guiGraphics.fill(
                screenX + VERTICAL_BORDER_DEPTH,
                screenY + HORIZONTAL_BORDER_DEPTH,
                screenX + VERTICAL_BORDER_DEPTH + SCREEN_WIDTH,
                screenY + HORIZONTAL_BORDER_DEPTH + SCREEN_HEIGHT,
                FastColor.ARGB32.color(70, 0, 0, 0)
            )
        }
        //render unblockable widgets
        renderWidgets(unblockable, guiGraphics, mouseX, mouseY, delta)
        poseStack.popPose()
        guiGraphics.disableScissor()
        poseStack.pushPose()
        poseStack.translate(0f, 0f, 2000f)
        renderBaseElement(poseStack, DETAILS)
        poseStack.popPose()
        poseStack.popPose()

        if (animationOffset > 0f) {
            animationOffset -= ANIMATION_SPEED * delta
            if (animationOffset < 0f) {
                animationOffset = 0f
            }
        }
//        guiGraphics.fill((width.toFloat() / 2f).toInt() - 1, 0, (width.toFloat() / 2f).toInt() + 1, height, FastColor.ARGB32.color(255, 255, 255, 255))
    }

    open fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {}

    open fun renderOnFrontLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {}

    private fun renderWidgets(widgets: List<AbstractWidget>, guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val iterator = widgets.iterator()
        while (iterator.hasNext()) {
            iterator.next().render(guiGraphics, mouseX, mouseY, delta)
        }
    }

    private fun renderBaseElement(poseStack: PoseStack, resourceLocation: ResourceLocation) {
        blitk(
            poseStack,
            texture = resourceLocation,
            x = screenX,
            y = screenY,
            width = WIDTH,
            height = HEIGHT
        )
    }

    private fun renderScreenBackground(guiGraphics: GuiGraphics, resourceLocation: ResourceLocation?, color: Int) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(0f, 0f, -100f)
        guiGraphics.fill(
            screenX + VERTICAL_BORDER_DEPTH,
            screenY + HORIZONTAL_BORDER_DEPTH,
            screenX + WIDTH - VERTICAL_BORDER_DEPTH,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH,
            color
        )
        poseStack.popPose()
//        blitk(
//            poseStack,
//            texture = resourceLocation,
//            x = screenX,
//            y = screenY,
//            width = WIDTH,
//            height = HEIGHT,
//            textureWidth = WIDTH,
//            textureHeight = HEIGHT,
////            red = FastColor.ARGB32.red(color),
////            green = FastColor.ARGB32.green(color),
////            blue = FastColor.ARGB32.blue(color),
//        )
        blitk(
            poseStack,
            texture = SCREEN_BASE,
            x = screenX,
            y = screenY,
            width = WIDTH,
            height = HEIGHT,
            red = FastColor.ARGB32.red(color) / 128f,
            green = FastColor.ARGB32.green(color) / 128f,
            blue = FastColor.ARGB32.blue(color) / 128f  ,
        )
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
        if (!blockWidgets) {
            val blockableClicked = blockable.widgetsClicked(d, e, i)
            if (blockableClicked) {
                return true
            }
        }
        return unblockable.widgetsClicked(d, e, i)
    }

    override fun mouseScrolled(d: Double, e: Double, f: Double, g: Double): Boolean {
        if (!blockWidgets) {
            val blockableScrolled = blockable.widgetsScrolled(d, e, f, g)
            if (blockableScrolled) {
                return true
            }
        }
        return unblockable.widgetsScrolled(d, e, f, g)
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        if (!blockWidgets) {
            blockable.widgetsDragged(d, e, i, f, g)
        }
        unblockable.widgetsDragged(d, e, i, f, g)
        return true
    }

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean {
        if (!blockWidgets) {
            blockable.widgetsReleased(d, e, i)
        }
        unblockable.widgetsReleased(d, e, i)
        return true
    }

    private fun List<AbstractWidget>.widgetsClicked(d: Double, e: Double, i: Int): Boolean {
        return this.any {
            it.mouseClicked(d, e, i)
        }
    }

    private fun List<AbstractWidget>.widgetsScrolled(d: Double, e: Double, f: Double, g: Double): Boolean {
        return this.any {
            it.mouseScrolled(d, e, f, g)
        }
    }

    private fun List<AbstractWidget>.widgetsDragged(d: Double, e: Double, i: Int, f: Double, g: Double) {
        this.forEach {
            it.mouseDragged(d, e, i, f, g)
        }
    }

    private fun List<AbstractWidget>.widgetsReleased(d: Double, e: Double, i: Int) {
        this.forEach {
            it.mouseReleased(d, e, i)
        }
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    fun addBlockableWidget(widget: AbstractWidget) {
        blockable.add(widget)
    }

    fun removeBlockableWidget(widget: AbstractWidget) {
        blockable.remove(widget)
    }

    fun clearBlockableWidgets() {
        blockable.clear()
    }

    fun addUnblockableWidget(widget: AbstractWidget) {
        unblockable.add(widget)
    }

    fun removeUnblockableWidget(widget: AbstractWidget) {
        unblockable.remove(widget)
    }

    fun clearUnblockableWidget() {
        unblockable.clear()
    }

    fun changeScreen(screen: PokenavScreen, savePrevious: Boolean = false) {
        onScreenChange()
        if (savePrevious) screen.previousScreen = this
        minecraft?.setScreen(screen)
    }

    override fun onClose() {
        onScreenChange()
        super.onClose()
    }

    open fun onScreenChange() {}
}