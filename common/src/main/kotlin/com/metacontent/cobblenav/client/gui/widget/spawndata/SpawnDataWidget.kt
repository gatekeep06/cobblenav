package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.api.platform.BiomePlatformRenderDataRepository
import com.metacontent.cobblenav.api.platform.DimensionPlateRepository
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import com.metacontent.cobblenav.client.gui.screen.SpawnDataDisplayer
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.spawndata.CheckedSpawnData
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
    val spawnData: CheckedSpawnData,
    private val displayer: SpawnDataDisplayer
) : SoundlessWidget(x, y, WIDTH, HEIGHT, Component.literal("Spawn Data Widget")) {
    companion object {
        const val WIDTH = 45
        const val HEIGHT = 45
        const val MODEL_HEIGHT = 35
        const val POKE_BALL_OFFSET = 6
        const val TRACK_SIZE = 12
        val FORMAT = DecimalFormat("#.##")
        val TRACK = gui("location/track")
        val BROKEN_MODEL = gui("location/broken_model")
    }

    private var isModelBroken = false
    protected open val platform = if (spawnData.data.result.shouldRenderPlatform()) {
        BiomePlatformRenderDataRepository.get(spawnData.platformId)
    } else if (spawnData.data.positionType == "fishing") {
        BiomePlatformRenderDataRepository.FISHING
    } else {
        BiomePlatformRenderDataRepository.EMPTY
    }
    protected open val plate = if (spawnData.data.result.shouldRenderPlatform()) {
        DimensionPlateRepository.get(Minecraft.getInstance().level?.dimension()?.location())
    } else {
        DimensionPlateRepository.EMPTY
    }
    private val stack by lazy { ItemStack(CobblemonItems.POKE_BALL) }
    private val shouldRenderPokeBall = spawnData.data.result.shouldRenderPokeBall()
    var nearbyEntityIds = emptyList<Int>()

    private val trackButton = IconButton(
        pX = x + width - TRACK_SIZE + 1,
        pY = y + 1,
        pWidth = TRACK_SIZE,
        pHeight = TRACK_SIZE,
        texture = TRACK,
        action = {
            val player = Minecraft.getInstance().player ?: return@IconButton
            val entities = nearbyEntityIds.mapNotNull { player.clientLevel?.getEntity(it) }
            CobblenavClient.trackArrowOverlay.entityId = entities.minByOrNull {
                it.distanceTo(player)
            }?.id ?: -1
            (Minecraft.getInstance().screen as? PokenavScreen)?.onClose()
        }
    )

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, delta: Float) {
        val poseStack = guiGraphics.pose()
        val hovered = ishHovered(i, j) && isFocused && !displayer.isBlockingTooltip()

        if (hovered && !trackButton.isHovered) {
            displayer.hoveredData = spawnData
        }

        platform.renderPlatform(
            poseStack = poseStack,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT,
            hovered = hovered
        )

        if (shouldRenderPokeBall) {
            renderPokeBall(
                guiGraphics = guiGraphics,
                x = x.toDouble() + POKE_BALL_OFFSET + platform.getPokemonXOffset(hovered),
                y = y.toDouble() + MODEL_HEIGHT / 2 + POKE_BALL_OFFSET + platform.getPokemonYOffset(hovered)
            )
        }

        if (nearby() && spawnData.data.result.shouldRenderPlatform()) {
            trackButton.render(guiGraphics, i, j, delta)
        }

        if (!isModelBroken) {
            try {
//                guiGraphics.fill(x, y, x + width, y + height, FastColor.ARGB32.color(100, 255, 255, 255))
                spawnData.data.result.drawResult(
                    poseStack = poseStack,
                    x = x.toFloat() + width / 2 + platform.getPokemonXOffset(hovered),
                    y = y.toFloat() + platform.getPokemonYOffset(hovered),
                    z = 300f,
                    delta = delta
                )
            } catch (_: Exception) {
                isModelBroken = true
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

        poseStack.pushAndPop(
            translate = Vector3d(0.0, 0.0, 300.0)
        ) {
            platform.renderDetails(
                poseStack = poseStack,
                x = x,
                y = y,
                width = WIDTH,
                height = HEIGHT,
                hovered = hovered
            )
            plate.render(
                poseStack = poseStack,
                x = x,
                y = y,
                width = WIDTH,
                height = HEIGHT,
                hovered = hovered
            )
            drawScaledText(
                guiGraphics,
                text = Component.literal(getChanceString()),
                x = x + width / 2, y = y + MODEL_HEIGHT + 0.75f,
                maxCharacterWidth = width,
                centered = true,
                shadow = true
            )
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (clicked(pMouseX, pMouseY) && isValidClickButton(pButton)) {
            trackButton.mouseClicked(pMouseX, pMouseY, pButton).takeIf { it }?.let {
                return true
            }
            displayer.selectedData = spawnData.data
            return true
        }
        return false
    }

    private fun getChanceString(): String {
        val finalChance = spawnData.chance * spawnData.chanceMultiplier
        return if (finalChance <= 0.005f) "<0.01%" else FORMAT.format(finalChance) + "%"
    }

    private fun renderPokeBall(guiGraphics: GuiGraphics, x: Double, y: Double) {
        val poseStack = guiGraphics.pose()

        poseStack.pushAndPop(
            translate = Vector3d(x, y, 2.0),
            mulPose = Quaternionf()
                .rotateZ(PI.toFloat())
                .fromEulerXYZDegrees(spawnData.data.result.getRotation()),
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

    fun nearby(): Boolean = nearbyEntityIds.isNotEmpty()

    override fun setX(i: Int) {
        trackButton.x += i - x
        super.setX(i)
    }

    override fun setY(i: Int) {
        trackButton.y += i - y
        super.setY(i)
    }
}