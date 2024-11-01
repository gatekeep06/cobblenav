package com.metacontent.cobblenav.client.gui.widget.location

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.onHover
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.render.drawScaledTextJustifiedRight
import com.metacontent.cobblenav.client.gui.widget.ClickableParentWidget
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

class LocationInfoWidget(
    x: Int, y: Int,
    biome: String
) : ClickableParentWidget(x, y, WIDTH, HEIGHT, Component.literal("Location Info")) {
    companion object {
        const val SYMBOL_WIDTH: Int = 13
        const val SYMBOL_HEIGHT: Int = 14
        const val SPACE: Int = 5
        const val BIOME_WIDTH: Int = 140
        const val WIDTH: Int = SYMBOL_WIDTH + SPACE + BIOME_WIDTH
        const val HEIGHT: Int = 14
        const val BIOME_KEY_BASE: String = "biome"
        val DAY = cobblenavResource("textures/gui/location/day_symbol.png")
        val NIGHT = cobblenavResource("textures/gui/location/night_symbol.png")
        val UNKNOWN_BIOME = cobblenavResource("textures/gui/location/unknown_biome.png")
    }

    private val biomeResourceLocation = ResourceLocation.parse(biome)

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()
        val checkPair = checkBiomeTranslation(String.format("%s.%s.%s", BIOME_KEY_BASE, biomeResourceLocation.namespace, biomeResourceLocation.path))
        if (!checkPair.first) {
            blitk(
                matrixStack = poseStack,
                texture = UNKNOWN_BIOME,
                x = x + BIOME_WIDTH - SYMBOL_WIDTH,
                y = y + (HEIGHT - SYMBOL_HEIGHT) / 2,
                width = SYMBOL_WIDTH,
                height = SYMBOL_HEIGHT
            )
        }
        drawScaledTextJustifiedRight(
            context = guiGraphics,
            text = checkPair.second,
            x = x + BIOME_WIDTH - if (!checkPair.first) (SYMBOL_WIDTH + SPACE) else 0,
            y = y + 3,
            maxCharacterWidth = BIOME_WIDTH
        )

        val isDay = ((Minecraft.getInstance().level?.dayTime ?: 0) % 24000) in 0..12999
        blitk(
            matrixStack = poseStack,
            texture = if (isDay) DAY else NIGHT,
            x = x + BIOME_WIDTH + SPACE,
            y = y + (HEIGHT - SYMBOL_HEIGHT) / 2,
            width = SYMBOL_WIDTH,
            height = SYMBOL_HEIGHT
        )

        if (isHovered && !checkPair.first) {
            guiGraphics.renderComponentHoverEffect(Minecraft.getInstance().font, checkPair.second.style, i - 100, j + height + 10)
        }
    }

    private fun checkBiomeTranslation(biomeKey: String): Pair<Boolean, MutableComponent> {
        val component = Component.translatable(biomeKey)
        if (component.string == biomeKey) {
            return Pair(false, Component.translatable("gui.cobblenav.unknown_biome").red().onHover(Component.literal(biomeResourceLocation.toString())))
        }
        return Pair(true, component)
    }
}