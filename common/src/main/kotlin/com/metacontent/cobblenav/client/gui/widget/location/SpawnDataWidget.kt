package com.metacontent.cobblenav.client.gui.widget.location

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.spawning.condition.SubmergedSpawningCondition
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.screen.FinderScreen
import com.metacontent.cobblenav.client.gui.screen.LocationScreen
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class SpawnDataWidget(
    x: Int, y: Int,
    val spawnData: SpawnData,
    private val parent: LocationScreen,
    chanceMultiplier: Float = 1f
) : SoundlessWidget(x, y, WIDTH, HEIGHT, Component.literal("Spawn Data Widget")) {
    companion object {
        const val WIDTH: Int = 40
        const val HEIGHT: Int = 50
        const val MODEL_HEIGHT: Int = 40
        val FORMAT = DecimalFormat("#.##")
        val BACKGROUND = cobblenavResource("textures/gui/location/pokeball_background.png")
        val BROKEN_MODEL = cobblenavResource("textures/gui/location/broken_model.png")
    }

    private var chanceString = ""
    var chanceMultiplier = chanceMultiplier
        set(value) {
            field = value
            val finalChance = spawnData.spawnChance * value
            chanceString = if (finalChance <= 0.005f) ">0.01%" else FORMAT.format(finalChance) + "%"
        }
    private val pose = if (spawnData.spawningContext == SubmergedSpawningCondition.NAME && CobblenavClient.config.useSwimmingAnimationIfSubmerged)
        PoseType.SWIM else PoseType.PROFILE
    private val state = FloatingState()
    private val obscured = !spawnData.encountered && CobblenavClient.config.obscureUnknownPokemon
    private var isModelBroken = false

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        if (ishHovered(i, j) && isFocused && !parent.blockWidgets) {
            blitk(
                matrixStack = poseStack,
                texture = BACKGROUND,
                x = x + 2,
                y = y + 2,
                width = MODEL_HEIGHT - 4,
                height = MODEL_HEIGHT - 4,
                alpha = 0.5f
            )
            parent.hoveredSpawnData = spawnData
        }
        if (!isModelBroken) {
            try {
                drawPokemon(
                    poseStack = poseStack,
                    pokemon = spawnData.renderable,
                    x = x.toFloat() + width / 2,
                    y = y.toFloat() + 8, // + if (spawnData.pose == PoseType.PROFILE) 8 else 0,
                    z = 100f,
                    delta = delta,
                    state = state,
                    poseType = pose,
                    obscured = obscured
                )
            }
            catch (e: IllegalArgumentException) {
                isModelBroken = true
                val message = Component.translatable(
                    "gui.cobblenav.pokemon_rendering_exception",
                    spawnData.renderable.species.translatedName.string,
                    spawnData.renderable.species.translatedName.string
                )
                Cobblenav.LOGGER.error(message.string)
                Cobblenav.LOGGER.error(e.message)
                if (CobblenavClient.config.sendErrorMessagesToChat) {
                    parent.player?.sendSystemMessage(message.withStyle(ChatFormatting.RED))
                }
            }
        }
        else {
            blitk(
                matrixStack = poseStack,
                texture = BROKEN_MODEL,
                x = x + 2,
                y = y + 2,
                width = MODEL_HEIGHT - 4,
                height = MODEL_HEIGHT - 4
            )
        }
        drawScaledText(
            guiGraphics,
            text = Component.literal(chanceString),
            x = x + width / 2, y = y + MODEL_HEIGHT,
            maxCharacterWidth = width,
            centered = true,
            shadow = true
        )
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (clicked(pMouseX, pMouseY) && isValidClickButton(pButton)) {
            parent.changeScreen(FinderScreen(spawnData, parent.os), true)
            return true
        }
        return false
    }
}