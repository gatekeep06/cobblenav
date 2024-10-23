package com.metacontent.cobblenav.client.gui.widget.location

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.screen.LocationScreen
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class BucketSelectorWidget(
    x: Int, y: Int,
    private val parent: LocationScreen
) : SoundlessWidget(x, y, WIDTH, HEIGHT, Component.literal("Bucket Selector")) {
    companion object {
        const val WIDTH: Int = 80
        const val HEIGHT: Int = 16
        const val BUTTON_WIDTH: Int = 10
        const val BUTTON_HEIGHT: Int = 9
        const val SPACE: Int = 1
        const val BUCKET_KEY_BASE: String = "bucket.cobblenav."
        val NEXT = cobblenavResource("textures/gui/button/next_button.png")
        val PREV = cobblenavResource("textures/gui/button/prev_button.png")
    }

    private val prevButton = IconButton(
        pX = x,
        pY = y + (height - BUTTON_HEIGHT) / 2,
        pWidth = BUTTON_WIDTH,
        pHeight = BUTTON_HEIGHT,
        disabled = parent.bucketIndex <= 0,
        action = { parent.bucketIndex-- },
        texture = PREV
    ).also { addWidget(it) }
    private val nextButton = IconButton(
        pX = x + WIDTH - BUTTON_WIDTH,
        pY = y + (height - BUTTON_HEIGHT) / 2,
        pWidth = BUTTON_WIDTH,
        pHeight = BUTTON_HEIGHT,
        disabled = parent.bucketIndex >= parent.buckets.size - 1,
        action = { parent.bucketIndex++ },
        texture = NEXT
    ).also { addWidget(it) }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        prevButton.disabled = parent.bucketIndex <= 0
        nextButton.disabled = parent.bucketIndex >= parent.buckets.size - 1
        prevButton.render(guiGraphics, i, j, f)
        drawScaledText(
            context = guiGraphics,
            text = Component.translatable(BUCKET_KEY_BASE + parent.currentBucket),
            x = x + WIDTH / 2,
            y = y + 3,
            centered = true,
            maxCharacterWidth = WIDTH - 2 * (BUTTON_WIDTH + SPACE)
        )
        nextButton.render(guiGraphics, i, j, f)
    }
}