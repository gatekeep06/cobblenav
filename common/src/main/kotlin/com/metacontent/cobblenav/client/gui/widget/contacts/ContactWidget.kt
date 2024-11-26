package com.metacontent.cobblenav.client.gui.widget.contacts

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class ContactWidget(
    x: Int, y: Int
) : SoundlessWidget(x, y, WIDTH, HEIGHT, Component.literal("Contact Widget")) {
    companion object {
        const val WIDTH: Int = 20
        const val HEIGHT: Int = 30
        const val HEAD_HEIGHT: Int = 20
        const val SKIN_HEAD_OFFSET: Int = 8
        const val SKIN_SIZE: Int = 64
    }

    private var skin: ResourceLocation? = null

    init {
        Minecraft.getInstance().skinManager.getOrLoad(Minecraft.getInstance().gameProfile).thenApply {
            skin = it.texture
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        val scale = HEAD_HEIGHT / 8f
        blitk(
            matrixStack = poseStack,
            texture = skin,
            x = x / scale,
            y = y / scale,
            width = 8,
            height = 8,
            vOffset = SKIN_HEAD_OFFSET,
            uOffset = SKIN_HEAD_OFFSET,
            textureWidth = SKIN_SIZE,
            textureHeight = SKIN_SIZE,
            scale = scale
        )
    }
}