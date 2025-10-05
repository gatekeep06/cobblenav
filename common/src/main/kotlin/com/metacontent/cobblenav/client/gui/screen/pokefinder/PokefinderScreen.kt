package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.pokefinder.FinderListEntryWidget
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class PokefinderScreen : Screen(Component.literal("Pokefinder")) {
    companion object {
        const val WIDTH: Int = 288
        const val HEIGHT: Int = 192
        const val LOGO_SIZE: Int = 64
        const val BORDER: Int = 5
        const val SETTING_WIDTH: Int = 180
        const val FIELD_HEIGHT: Int = 20
        const val CHECK_BOX_HEIGHT: Int = 10
        const val X_OFFSET: Int = 4
        const val Y_TEXT_OFFSET: Int = 4
        const val Y_FIELD_OFFSET: Int = 14
        const val Y_CHECK_BOX_OFFSET: Int = 10
        val BACKGROUND = gui("pokefinder/background")
        val LOGO = gui("pokefinder/logo")
        val ADD = gui("pokefinder/add")
    }

    val player: LocalPlayer? = Minecraft.getInstance().player

    init {
        player?.playSound(CobblemonSounds.PC_ON, 0.1f, 2f)
    }

    private var screenX = 0
    private var screenY = 0

    val settigns = CobblenavClient.pokefinderSettings

    private lateinit var scrollableView: ScrollableView
    private lateinit var tableView: TableView<ScrollableItemWidget<*>>

    override fun init() {
        screenX = (width - WIDTH) / 2
        screenY = (height - HEIGHT) / 2

        tableView = TableView(
            x = screenX + WIDTH - SETTING_WIDTH - BORDER,
            y = screenY + BORDER + 20,
            width = SETTING_WIDTH,
            columns = 1,
            verticalGap = 6f,
            horizontalGap = 0f
        )
        initEntries()

        scrollableView = ScrollableView(
            x = tableView.x,
            y = tableView.y,
            width = tableView.width,
            height = HEIGHT - 2 * BORDER - 40,
            child = tableView
        ).also { addWidget(it) }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        renderBackground(guiGraphics, mouseX, mouseY, delta)
        blitk(
            matrixStack = poseStack,
            texture = BACKGROUND,
            x = screenX,
            y = screenY,
            width = WIDTH,
            height = HEIGHT
        )

        scrollableView.render(guiGraphics, mouseX, mouseY, delta)

        blitk(
            matrixStack = poseStack,
            texture = LOGO,
            x = screenX + (WIDTH - LOGO_SIZE - SETTING_WIDTH) / 2,
            y = screenY + (HEIGHT - LOGO_SIZE) / 2 - 20,
            width = LOGO_SIZE,
            height = LOGO_SIZE,
            alpha = 0.8f
        )
        drawScaledText(
            context = guiGraphics,
            text = Component.translatable("item.cobblenav.pokefinder_item").bold(),
            x = screenX + (WIDTH - SETTING_WIDTH) / 2,
            y = screenY + HEIGHT / 2 + 16,
            centered = true,
            colour = FastColor.ARGB32.color(200, 31, 90, 91)
        )
    }

    fun initEntries() {
        tableView.clear()
        val entries: MutableList<ScrollableItemWidget<*>> = mutableListOf()
        settigns?.finderEntries?.map { entry ->
            ScrollableItemWidget(
                child = FinderListEntryWidget(
                    x = 0,
                    y = 0,
                    width = tableView.width - 10,
                    height = FIELD_HEIGHT,
                    entry = entry,
                    holder = this
                ),
                topEdge = screenY + BORDER,
                bottomEdge = screenY + HEIGHT - BORDER
            )
        }?.also { entries.addAll(it) }
        ScrollableItemWidget(
            child = IconButton(
                pX = 0,
                pY = 0,
                pWidth = tableView.width,
                pHeight = FIELD_HEIGHT,
                texture = ADD,
                action = {
                    settigns?.addEntry(PokemonProperties())
                    initEntries()
                }
            ),
            topEdge = screenY + BORDER,
            bottomEdge = screenY + HEIGHT - BORDER
        ).also { entries.add(it) }
        tableView.add(entries)
    }

    fun removeEntry(entry: PokemonProperties) {
        settigns?.removeEntry(entry)
        initEntries()
        scrollableView.reset()
    }

    override fun isPauseScreen(): Boolean = false

    override fun keyPressed(i: Int, j: Int, k: Int): Boolean {
        if (i == InputConstants.KEY_ESCAPE && shouldCloseOnEsc()) {
            tableView.applyToAll {
                if (it.child is FinderListEntryWidget && it.child.textField.isFocused) {
                    it.child.textField.finish()
                }
            }
        }
        return super.keyPressed(i, j, k)
    }
}