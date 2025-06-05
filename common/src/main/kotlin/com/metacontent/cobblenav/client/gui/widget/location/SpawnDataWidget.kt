package com.metacontent.cobblenav.client.gui.widget.location

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition
import com.cobblemon.mod.common.api.spawning.condition.SubmergedSpawningCondition
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.api.platform.BiomePlatformRenderDataRepository
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.screen.SpawnDataTooltipDisplayer
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.joml.Quaternionf
import org.joml.Vector3f
import java.text.DecimalFormat

class SpawnDataWidget(
    x: Int,
    y: Int,
    val spawnData: SpawnData,
    private val displayer: SpawnDataTooltipDisplayer,
    private val onClick: (SpawnDataWidget) -> Unit = {},
    private val pose: PoseType = if (spawnData.spawningContext == SubmergedSpawningCondition.NAME && CobblenavClient.config.useSwimmingAnimationIfSubmerged) PoseType.SWIM else PoseType.PROFILE,
    private val pokemonRotation: Vector3f = Vector3f(15F, 35F, 0F),
    chanceMultiplier: Float = 1f
) : SoundlessWidget(x, y, WIDTH, HEIGHT, Component.literal("Spawn Data Widget")) {
    companion object {
        const val WIDTH = 45
        const val HEIGHT = 45
        const val MODEL_HEIGHT = 35
        val FORMAT = DecimalFormat("#.##")
        val BROKEN_MODEL = cobblenavResource("textures/gui/location/broken_model.png")
    }

    private var chanceString = getChanceString(chanceMultiplier)
    var chanceMultiplier = chanceMultiplier
        set(value) {
            field = value
            chanceString = getChanceString(value)
        }
    private val state = FloatingState()
    private val obscured = !spawnData.encountered && CobblenavClient.config.obscureUnknownPokemon
    private var isModelBroken = false
    private val isFishing = spawnData.spawningContext == FishingSpawningCondition.NAME
    private val platform = BiomePlatformRenderDataRepository.get(spawnData.platform)

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        val selected = ishHovered(i, j) && isFocused && !displayer.isBlockingTooltip()

        if (selected) {
            displayer.hoveredWidget = this
        }

        if (!isFishing) {
            blitk(
                matrixStack = poseStack,
                texture = if (selected) platform.selectedBackground else platform.background,
                x = x,
                y = y,
                width = WIDTH,
                height = HEIGHT
            )
        }

        if (!isModelBroken) {
            try {
//                guiGraphics.fill(x, y, x + width, y + height, FastColor.ARGB32.color(100, 255, 255, 255))
                drawPokemon(
                    poseStack = poseStack,
                    pokemon = spawnData.renderable,
                    x = x.toFloat() + width / 2,
                    y = y.toFloat() + 1 - (if (selected && !isFishing) platform.selectedPokemonOffset else 0),
                    z = 100f,
                    delta = delta,
                    state = state,
                    poseType = pose,
                    rotation = Quaternionf().fromEulerXYZDegrees(pokemonRotation),
                    obscured = obscured
                )
            } catch (e: IllegalArgumentException) {
                isModelBroken = true
                val message = Component.translatable(
                    "gui.cobblenav.pokemon_rendering_exception",
                    spawnData.renderable.species.translatedName.string,
                    spawnData.renderable.species.translatedName.string
                )
                Cobblenav.LOGGER.error(message.string)
                Cobblenav.LOGGER.error(e.message)
                if (CobblenavClient.config.sendErrorMessagesToChat) {
                    Minecraft.getInstance().player?.sendSystemMessage(message.red())
                }
            }
        } else {
            blitk(
                matrixStack = poseStack,
                texture = BROKEN_MODEL,
                x = x + 2,
                y = y + 2,
                width = MODEL_HEIGHT - 4,
                height = MODEL_HEIGHT - 4
            )
        }

        if (!isFishing) {
            blitk(
                matrixStack = poseStack,
                texture = if (selected) platform.selectedForeground else platform.foreground,
                x = x,
                y = y,
                width = WIDTH,
                height = HEIGHT
            )
        }

        drawScaledText(
            guiGraphics,
            text = Component.literal(chanceString),
            x = x + width / 2, y = y + MODEL_HEIGHT + 0.75f,
            maxCharacterWidth = width,
            centered = true,
            shadow = true
        )
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (clicked(pMouseX, pMouseY) && isValidClickButton(pButton)) {
            onClick.invoke(this)
            return true
        }
        return false
    }

    private fun getChanceString(chanceMultiplier: Float): String {
        val finalChance = spawnData.spawnChance * chanceMultiplier
        return if (finalChance <= 0.005f) ">0.01%" else FORMAT.format(finalChance) + "%"
    }
}