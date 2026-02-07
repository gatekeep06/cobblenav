package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget
import com.metacontent.cobblenav.client.gui.widget.button.CheckBox
import com.metacontent.cobblenav.util.cobblenavResource
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
        const val TEXTFIELD_WIDTH: Int = 160
        const val CHECKBOX_WIDTH: Int = 100
        const val FIELD_HEIGHT: Int = 20
        const val CHECK_BOX_HEIGHT: Int = 10
        const val X_OFFSET: Int = 4
        const val Y_TEXT_OFFSET: Int = 4
        const val Y_FIELD_OFFSET: Int = 14
        const val Y_CHECK_BOX_OFFSET: Int = 10
        const val MID_OFFSET: Int = WIDTH/2
        val BACKGROUND = gui("pokefinder/background")
        val LOGO = gui("pokefinder/logo")
    }

    val player: LocalPlayer? = Minecraft.getInstance().player
    init {
        player?.playSound(CobblemonSounds.PC_ON, 0.1f, 2f)
    }

    private var screenX = 0
    private var screenY = 0

    private lateinit var speciesField: TextFieldWidget
    private lateinit var aspectsField: TextFieldWidget
    private lateinit var labelsField: TextFieldWidget

    private lateinit var strictAspectCheckBox: CheckBox
    private lateinit var strictLabelCheckBox: CheckBox
    private lateinit var shinyOnlyCheckBox: CheckBox

    private lateinit var highlightShinyCheckBox: CheckBox
    private lateinit var showFilteredCheckBox: CheckBox
    private lateinit var uncaughtOnlyCheckBox: CheckBox

    private val settings = CobblenavClient.pokefinderSettings

    override fun init() {
        screenX = (width - WIDTH) / 2
        screenY = (height - HEIGHT) / 2

        speciesField = TextFieldWidget(
            fieldX = screenX + BORDER + X_OFFSET,
            fieldY = screenY + BORDER + Y_FIELD_OFFSET,
            width = TEXTFIELD_WIDTH,
            height = FIELD_HEIGHT,
            default = settings?.species?.joinToString(separator = ", ") ?: "",
            fillColor = FastColor.ARGB32.color(255, 20, 60, 61),
            outlineColor = FastColor.ARGB32.color(255, 31, 90, 91),
            focusedOutlineColor = FastColor.ARGB32.color(255, 84, 146, 147),
            hint = Component.literal("Sableye, ..."),
            onFinish = { value ->
                settings?.let {
                    it.species = value.splitToSet()
                }
            }
        ).also { addWidget(it) }
        aspectsField = TextFieldWidget(
            fieldX = screenX + BORDER + X_OFFSET,
            fieldY = screenY + BORDER + 2 * Y_FIELD_OFFSET + FIELD_HEIGHT,
            width = TEXTFIELD_WIDTH,
            height = FIELD_HEIGHT,
            default = settings?.aspects?.joinToString(separator = ", ") ?: "",
            fillColor = FastColor.ARGB32.color(255, 20, 60, 61),
            outlineColor = FastColor.ARGB32.color(255, 31, 90, 91),
            focusedOutlineColor = FastColor.ARGB32.color(255, 84, 146, 147),
            hint = Component.literal("Galarian, ..."),
            onFinish = { value ->
                settings?.let {
                    it.aspects = value.splitToSet()
                }
            }
        ).also { addWidget(it) }
        labelsField = TextFieldWidget(
            fieldX = screenX + BORDER + X_OFFSET,
            fieldY = screenY + BORDER + 3 * Y_FIELD_OFFSET + 2 * FIELD_HEIGHT,
            width = TEXTFIELD_WIDTH,
            height = FIELD_HEIGHT,
            default = settings?.labels?.joinToString(separator = ", ") ?: "",
            fillColor = FastColor.ARGB32.color(255, 20, 60, 61),
            outlineColor = FastColor.ARGB32.color(255, 31, 90, 91),
            focusedOutlineColor = FastColor.ARGB32.color(255, 84, 146, 147),
            hint = Component.literal("Legendary, ..."),
            onFinish = { value ->
                settings?.let {
                    it.labels = value.splitToSet()
                }
            }
        ).also { addWidget(it) }

        strictAspectCheckBox = CheckBox(
            pX = screenX + BORDER + X_OFFSET,
            pY = screenY + BORDER + 3 * Y_FIELD_OFFSET + 3 * FIELD_HEIGHT + Y_CHECK_BOX_OFFSET,
            pWidth = CHECKBOX_WIDTH,
            pHeight = CHECK_BOX_HEIGHT,
            textOffset = 6,
            text = Component.translatable("gui.cobblenav.strict_aspect_check"),
            shadow = true,
            default = settings?.strictAspectCheck ?: false,
            afterClick = { button -> settings?.let { it.strictAspectCheck = (button as CheckBox).checked } }
        ).also { addWidget(it) }
        strictLabelCheckBox = CheckBox(
            pX = screenX + BORDER + X_OFFSET + MID_OFFSET,
            pY = screenY + BORDER + 3 * Y_FIELD_OFFSET + 3 * FIELD_HEIGHT + Y_CHECK_BOX_OFFSET,
            pWidth = CHECKBOX_WIDTH,
            pHeight = CHECK_BOX_HEIGHT,
            textOffset = 6,
            text = Component.translatable("gui.cobblenav.strict_label_check"),
            shadow = true,
            default = settings?.strictLabelCheck ?: false,
            afterClick = { button -> settings?.let { it.strictLabelCheck = (button as CheckBox).checked } }
        ).also { addWidget(it) }
        shinyOnlyCheckBox = CheckBox(
            pX = screenX + BORDER + X_OFFSET,
            pY = screenY + BORDER + 3 * Y_FIELD_OFFSET + 3 * FIELD_HEIGHT + 2 * Y_CHECK_BOX_OFFSET + CHECK_BOX_HEIGHT,
            pWidth = CHECKBOX_WIDTH,
            pHeight = CHECK_BOX_HEIGHT,
            textOffset = 6,
            text = Component.translatable("gui.cobblenav.shiny_only"),
            shadow = true,
            default = settings?.shinyOnly ?: false,
            afterClick = { button -> settings?.let { it.shinyOnly = (button as CheckBox).checked } }
        ).also { addWidget(it) }

        highlightShinyCheckBox = CheckBox(
            pX = screenX + BORDER + X_OFFSET + MID_OFFSET,
            pY = screenY + BORDER + 3 * Y_FIELD_OFFSET + 3 * FIELD_HEIGHT + 2 * Y_CHECK_BOX_OFFSET + CHECK_BOX_HEIGHT,
            pWidth = CHECKBOX_WIDTH,
            pHeight = CHECK_BOX_HEIGHT,
            textOffset = 6,
            text = Component.translatable("gui.cobblenav.highlight_shiny"),
            shadow = true,
            default = settings?.highlightShiny ?: true,
            afterClick = { button -> settings?.let { it.highlightShiny = (button as CheckBox).checked } }
        ).also { addWidget(it) }
        uncaughtOnlyCheckBox = CheckBox(
            pX = screenX + BORDER + X_OFFSET,
            pY = screenY + BORDER + 3 * Y_FIELD_OFFSET + 3 * FIELD_HEIGHT + 3 * Y_CHECK_BOX_OFFSET + 2 * CHECK_BOX_HEIGHT,
            pWidth = CHECKBOX_WIDTH,
            pHeight = CHECK_BOX_HEIGHT,
            textOffset = 6,
            text = Component.translatable("gui.cobblenav.uncaught_only"),
            shadow = true,
            default = settings?.uncaughtOnly ?: true,
            afterClick = { button -> settings?.let { it.uncaughtOnly = (button as CheckBox).checked } }
        ).also { addWidget(it) }
        showFilteredCheckBox = CheckBox(
            pX = screenX + BORDER + X_OFFSET + MID_OFFSET,
            pY = screenY + BORDER + 3 * Y_FIELD_OFFSET + 3 * FIELD_HEIGHT + 3 * Y_CHECK_BOX_OFFSET + 2 * CHECK_BOX_HEIGHT,
            pWidth = CHECKBOX_WIDTH,
            pHeight = CHECK_BOX_HEIGHT,
            textOffset = 6,
            text = Component.translatable("gui.cobblenav.show_filtered"),
            shadow = true,
            default = settings?.showFiltered ?: true,
            afterClick = { button -> settings?.let { it.showFiltered = (button as CheckBox).checked } }
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
        drawScaledText(
            context = guiGraphics,
            text = Component.translatable("gui.cobblenav.species"),
            x = screenX + BORDER + X_OFFSET,
            y = screenY + BORDER + Y_TEXT_OFFSET,
            shadow = true
        )
        speciesField.render(guiGraphics, mouseX, mouseY, delta)
        drawScaledText(
            context = guiGraphics,
            text = Component.translatable("gui.cobblenav.aspects"),
            x = screenX + BORDER + X_OFFSET,
            y = screenY + BORDER + Y_FIELD_OFFSET + FIELD_HEIGHT + Y_TEXT_OFFSET,
            shadow = true
        )
        aspectsField.render(guiGraphics, mouseX, mouseY, delta)
        drawScaledText(
            context = guiGraphics,
            text = Component.translatable("gui.cobblenav.labels"),
            x = screenX + BORDER + X_OFFSET,
            y = screenY + BORDER + 2 * Y_FIELD_OFFSET + 2 * FIELD_HEIGHT + Y_TEXT_OFFSET,
            shadow = true
        )
        labelsField.render(guiGraphics, mouseX, mouseY, delta)

        strictAspectCheckBox.render(guiGraphics, mouseX, mouseY, delta)
        strictLabelCheckBox.render(guiGraphics, mouseX, mouseY, delta)
        shinyOnlyCheckBox.render(guiGraphics, mouseX, mouseY, delta)

        highlightShinyCheckBox.render(guiGraphics, mouseX, mouseY, delta)
        uncaughtOnlyCheckBox.render(guiGraphics, mouseX, mouseY, delta)
        showFilteredCheckBox.render(guiGraphics, mouseX, mouseY, delta)

        blitk(
            matrixStack = poseStack,
            texture = LOGO,
            x = screenX + BORDER + (WIDTH - LOGO_SIZE + TEXTFIELD_WIDTH) / 2,
            y = screenY + (HEIGHT - LOGO_SIZE) / 2 - 40,
            width = LOGO_SIZE,
            height = LOGO_SIZE,
            alpha = 0.8f
        )
        drawScaledText(
            context = guiGraphics,
            text = Component.translatable("item.cobblenav.pokefinder_item").bold(),
            x = screenX + BORDER + (WIDTH + TEXTFIELD_WIDTH) / 2,
            y = screenY + HEIGHT / 2 - 4,
            centered = true,
            colour = FastColor.ARGB32.color(200, 31, 90, 91)
        )
    }

    override fun isPauseScreen(): Boolean = false

    override fun keyPressed(i: Int, j: Int, k: Int): Boolean {
        if (i == InputConstants.KEY_ESCAPE && shouldCloseOnEsc()) {
            speciesField.finish()
            aspectsField.finish()
            labelsField.finish()
        }
        return super.keyPressed(i, j, k)
    }

    private fun String.splitToSet() = this.split(", ", ",").filter(String::isNotBlank).map(String::trim).toSet()
}