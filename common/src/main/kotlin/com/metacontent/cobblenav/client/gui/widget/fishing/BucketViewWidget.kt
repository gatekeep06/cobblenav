package com.metacontent.cobblenav.client.gui.widget.fishing

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.util.*
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataWidget
import com.metacontent.cobblenav.util.WeightedBucket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import org.joml.Vector3d
import kotlin.math.max

class BucketViewWidget(
    x: Int,
    y: Int,
    width: Int,
    columns: Int,
    columnWidth: Int = width / columns,
    verticalPadding: Float = 0f,
    horizontalPadding: Float = (width - columns * columnWidth) / (columns - 1f),
    val minHeight: Int,
    val depthProgress: Float,
    val bucket: String
) : TableView<ScrollableItemWidget<SpawnDataWidget>>(
    x = x,
    y = y,
    width = width,
    columns = columns,
    columnWidth = columnWidth,
    verticalGap = verticalPadding,
    horizontalGap = horizontalPadding
) {
    companion object {
        const val SEPARATOR_HEIGHT = 4
        const val BUCKET_PADDING = 4
        const val BUCKET_WIDTH = 60
        const val DITHERING_HEIGHT = 20
        val UP_DAY_COLOR = RGB(0, 104, 179)
        val UP_NIGHT_COLOR = RGB(0, 51, 102)
        val DOWN_COLOR = RGB(0, 0, 0)
        val DITHERING = gui("fishing/dithering")
    }

    val color
        get() = interpolate(
            dayCycleColor(
                Minecraft.getInstance().level?.dayTime ?: 0L,
                UP_DAY_COLOR,
                UP_NIGHT_COLOR
            ),
            DOWN_COLOR,
            depthProgress
        )

    init {
        height = minHeight
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        guiGraphics.fill(
            x,
            y,
            x + width,
            y + height,
            color.toColor()
        )
        if (depthProgress != 0f) {
            blitk(
                matrixStack = poseStack,
                texture = DITHERING,
                x = x,
                y = y - DITHERING_HEIGHT,
                width = width,
                height = DITHERING_HEIGHT,
                red = color.red(),
                green = color.green(),
                blue = color.blue()
            )
        }
        poseStack.pushAndPop(
            translate = Vector3d(0.0, 0.0, 200.0)
        ) {
            guiGraphics.fill(
                x,
                y - SEPARATOR_HEIGHT / 2,
                x + width - BUCKET_WIDTH - BUCKET_PADDING,
                y + SEPARATOR_HEIGHT / 2,
                FastColor.ARGB32.color(225, 225, 225, 225)
            )
            drawScaledText(
                context = guiGraphics,
                text = Component.translatable("bucket.cobblenav.${bucket}"),
                x = x + width - BUCKET_WIDTH,
                y = y - SEPARATOR_HEIGHT - 0.4,
                shadow = true,
                maxCharacterWidth = BUCKET_WIDTH
            )
        }
        super.renderWidget(guiGraphics, i, j, f)
    }

    override fun calculateItems() {
        super.calculateItems()
        height = max(height + DITHERING_HEIGHT, minHeight)
    }
}