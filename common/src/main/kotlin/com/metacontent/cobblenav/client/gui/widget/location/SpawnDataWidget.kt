package com.metacontent.cobblenav.client.gui.widget.location

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
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
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f
import java.text.DecimalFormat
import kotlin.math.PI

open class SpawnDataWidget(
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
        const val POKE_BALL_OFFSET = 6
        val FORMAT = DecimalFormat("#.##")
        val BROKEN_MODEL = gui("location/broken_model")
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
    protected open val platform = BiomePlatformRenderDataRepository.get(spawnData.platform)
    private val stack by lazy { ItemStack(CobblemonItems.POKE_BALL) }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        val selected = ishHovered(i, j) && isFocused && !displayer.isBlockingTooltip()

        if (selected) {
            displayer.hoveredWidget = this
        }

        platform.getBackground(selected)?.let {
            blitk(
                matrixStack = poseStack,
                texture = it,
                x = x,
                y = y,
                width = WIDTH,
                height = HEIGHT
            )
            if (spawnData.knowledge == PokedexEntryProgress.CAUGHT) {
                renderPokeBall(
                    guiGraphics = guiGraphics,
                    x = x.toDouble() + POKE_BALL_OFFSET,
                    y = y.toDouble() + MODEL_HEIGHT / 2 + POKE_BALL_OFFSET - platform.getOffset(selected)
                )
            }
        }

        if (!isModelBroken) {
            try {
//                guiGraphics.fill(x, y, x + width, y + height, FastColor.ARGB32.color(100, 255, 255, 255))
                drawPokemon(
                    poseStack = poseStack,
                    pokemon = spawnData.renderable,
                    x = x.toFloat() + width / 2,
                    y = y.toFloat() - platform.getOffset(selected),
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

        platform.getForeground(selected)?.let {
            poseStack.pushAndPop(
                translate = Vector3d(0.0, 0.0, 300.0)
            ) {
                blitk(
                    matrixStack = poseStack,
                    texture = it,
                    x = x,
                    y = y,
                    width = WIDTH,
                    height = HEIGHT
                )
            }
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

    private fun renderPokeBall(guiGraphics: GuiGraphics, x: Double, y: Double) {
        val poseStack = guiGraphics.pose()

        poseStack.pushAndPop(
            translate = Vector3d(x, y, 2.0),
            mulPose = Quaternionf()
                .rotateZ(PI.toFloat())
                .fromEulerXYZDegrees(pokemonRotation),
            scale = Vector3f(15f, 15f, -15f)
        ) {
            Minecraft.getInstance().itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                255,
                1000,
                poseStack,
                guiGraphics.bufferSource(),
                Minecraft.getInstance().level,
                0
            )
        }
    }
}