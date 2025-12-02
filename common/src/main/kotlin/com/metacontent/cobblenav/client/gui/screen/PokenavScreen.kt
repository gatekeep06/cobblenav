package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.CobblemonRenderable
import com.google.common.collect.Lists
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.cobblenavScissor
import com.metacontent.cobblenav.client.gui.util.drawBlurredArea
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.NotificationWidget
import com.metacontent.cobblenav.os.PokenavOS
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor
import org.joml.Vector3f

abstract class PokenavScreen(
    val os: PokenavOS,
    makeOpeningSound: Boolean,
    animateOpening: Boolean,
    component: Component
) : Screen(component), CobblemonRenderable {
    companion object {
        const val WIDTH = 350
        const val HEIGHT = 250
        const val VERTICAL_BORDER_DEPTH = 21
        const val HORIZONTAL_BORDER_DEPTH = 16
        const val SCREEN_WIDTH = WIDTH - 2 * VERTICAL_BORDER_DEPTH
        const val SCREEN_HEIGHT = HEIGHT - 2 * HORIZONTAL_BORDER_DEPTH
        const val ANIMATION_SPEED = 20f
        const val ANIMATION_OFFSET = 20f
        const val BACK_BUTTON_SIZE = 14
        val DETAILS = gui("pokenav_details")
        val SCREEN_GLOW = gui("pokenav_screen_glow")
        val BORDERS = gui("pokenav_borders")
        val SCREEN = gui("pokenav_screen")
        val BACK_BUTTON = gui("button/back")
        val SUPPORT = gui("button/support_button")
    }

    val scale = CobblenavClient.config.screenScale
    var screenX = 0
    var screenY = 0
    abstract val color: Int
    val player: LocalPlayer? = Minecraft.getInstance().player
    private var animationOffset: Float = if (animateOpening) ANIMATION_OFFSET else 0f
    var blockWidgets: Boolean = false
    private val blockable = Lists.newArrayList<AbstractWidget>()
    private val unblockable = Lists.newArrayList<AbstractWidget>()
    lateinit var notifications: NotificationWidget
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

        width = (width / scale).toInt()
        height = (height / scale).toInt()

        screenX = (width - WIDTH) / 2
        screenY = (height - HEIGHT) / 2

        notifications = NotificationWidget(
            screenX + VERTICAL_BORDER_DEPTH,
            screenY + HORIZONTAL_BORDER_DEPTH
        ).also { addUnblockableWidget(it) }

        initScreen()
    }

    abstract fun initScreen()

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()

        poseStack.pushAndPop(
            scale = Vector3f(scale, scale, 1f)
        ) {
            renderBackground(guiGraphics, mouseX, mouseY, delta)
            val scaledMouseX = (mouseX / scale).toInt()
            val scaledMouseY = (mouseY / scale).toInt()
            poseStack.translate(0f, animationOffset, 100f)
            renderScreenBackground(guiGraphics, SCREEN, color)
            guiGraphics.cobblenavScissor(
                screenX + VERTICAL_BORDER_DEPTH,
                screenY + HORIZONTAL_BORDER_DEPTH - 1,
                screenX + VERTICAL_BORDER_DEPTH + SCREEN_WIDTH,
                screenY + HORIZONTAL_BORDER_DEPTH + SCREEN_HEIGHT + 1,
            )
            //render blockable widgets and the current screen's stuff
            renderOnBackLayer(guiGraphics, scaledMouseX, scaledMouseY, delta)
            renderWidgets(blockable, guiGraphics, scaledMouseX, scaledMouseY, delta)
            renderOnFrontLayer(guiGraphics, scaledMouseX, scaledMouseY, delta)
            // if true block widgets and screen
            poseStack.translate(0f, 0f, 500f)
            if (blockWidgets) {
                guiGraphics.fill(
                    screenX + VERTICAL_BORDER_DEPTH,
                    screenY + HORIZONTAL_BORDER_DEPTH,
                    screenX + VERTICAL_BORDER_DEPTH + SCREEN_WIDTH,
                    screenY + HORIZONTAL_BORDER_DEPTH + SCREEN_HEIGHT,
                    FastColor.ARGB32.color(70, 0, 0, 0)
                )
                guiGraphics.drawBlurredArea(
                    x1 = screenX + VERTICAL_BORDER_DEPTH + 1,
                    y1 = screenY + HORIZONTAL_BORDER_DEPTH - 1,
                    x2 = screenX + VERTICAL_BORDER_DEPTH + SCREEN_WIDTH - 1,
                    y2 = screenY + HORIZONTAL_BORDER_DEPTH + SCREEN_HEIGHT + 1,
                    blur = 3f,
                    delta = delta
                )
            }
            poseStack.translate(0f, 0f, 5000f)
            //render unblockable widgets
            renderWidgets(unblockable, guiGraphics, scaledMouseX, scaledMouseY, delta)

            guiGraphics.disableScissor()

            poseStack.translate(0f, 0f, 600f)
            renderBaseElement(poseStack, BORDERS)
            blitk(
                poseStack,
                texture = SCREEN_GLOW,
                x = screenX,
                y = screenY,
                width = WIDTH,
                height = HEIGHT,
                red = FastColor.ARGB32.red(color) / 128f,
                green = FastColor.ARGB32.green(color) / 128f,
                blue = FastColor.ARGB32.blue(color) / 128f,
            )
            poseStack.translate(0f, 0f, 900f)
            renderBaseElement(poseStack, DETAILS)
        }

        if (animationOffset > 0f) {
            animationOffset -= ANIMATION_SPEED * delta
            if (animationOffset < 0f) {
                animationOffset = 0f
            }
        }
//        guiGraphics.fill((width.toFloat() / 2f).toInt() - 1, 0, (width.toFloat() / 2f).toInt() + 1, height, FastColor.ARGB32.color(255, 255, 255, 255))
//        guiGraphics.fill(0, (height.toFloat() / 2f).toInt() - 1, width, (height.toFloat() / 2f).toInt() + 1, FastColor.ARGB32.color(255, 255, 255, 255))
    }

    open fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {}

    open fun renderOnFrontLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {}

    private fun renderWidgets(
        widgets: List<AbstractWidget>,
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
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
        guiGraphics.fill(
            screenX + VERTICAL_BORDER_DEPTH,
            screenY + HORIZONTAL_BORDER_DEPTH - 1,
            screenX + WIDTH - VERTICAL_BORDER_DEPTH,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH + 1,
            color
        )
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
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
        if (!blockWidgets) {
            val blockableClicked = blockable.widgetsClicked(d / scale, e / scale, i)
            if (blockableClicked) {
                return true
            }
        }
        return unblockable.widgetsClicked(d / scale, e / scale, i)
    }

    override fun mouseScrolled(d: Double, e: Double, f: Double, g: Double): Boolean {
        if (!blockWidgets) {
            val blockableScrolled = blockable.widgetsScrolled(d / scale, e / scale, f / scale, g / scale)
            if (blockableScrolled) {
                return true
            }
        }
        return unblockable.widgetsScrolled(d / scale, e / scale, f / scale, g / scale)
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        if (!blockWidgets) {
            blockable.widgetsDragged(d / scale, e / scale, i, f / scale, g / scale)
        }
        unblockable.widgetsDragged(d / scale, e / scale, i, f / scale, g / scale)
        return true
    }

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean {
        if (!blockWidgets) {
            blockable.widgetsReleased(d / scale, e / scale, i)
        }
        unblockable.widgetsReleased(d / scale, e / scale, i)
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

    fun toPreviousScreen() {
        minecraft?.screen = previousScreen
    }

    override fun onClose() {
        onScreenChange()
        super.onClose()
    }

    open fun onScreenChange() {}
}