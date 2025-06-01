package com.metacontent.cobblenav.client.gui.widget.fishing

import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.util.interpolate
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.location.SpawnDataWidget
import com.metacontent.cobblenav.util.WeightedBucket
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import kotlin.math.max
import kotlin.math.sqrt

class BucketViewWidget(
    x: Int,
    y: Int,
    width: Int,
    columns: Int,
    columnWidth: Int = width / columns,
    verticalPadding: Float = 0f,
    horizontalPadding: Float = (width - columns * columnWidth) / (columns - 1f),
    val minHeight: Int,
    val bucket: WeightedBucket
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
        val UP_COLOR = RGB(0, 64, 128)
        val DOWN_COLOR = RGB(0, 13, 25)
    }

    val color = interpolate(DOWN_COLOR, UP_COLOR, sqrt(bucket.chance)).toColor(200)

    init {
        height = minHeight
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.fill(
            x,
            y,
            x + width,
            y + height,
            color
        )
        guiGraphics.fill(
            x,
            y - SEPARATOR_HEIGHT / 2,
            x + width - BUCKET_WIDTH - BUCKET_PADDING,
            y + SEPARATOR_HEIGHT / 2,
            FastColor.ARGB32.color(225, 225, 225, 225)
        )
        drawScaledText(
            context = guiGraphics,
            text = Component.translatable("bucket.cobblenav.${bucket.name}"),
            x = x + width - BUCKET_WIDTH,
            y = y - SEPARATOR_HEIGHT - 0.4,
            shadow = true,
            maxCharacterWidth = BUCKET_WIDTH
        )
        super.renderWidget(guiGraphics, i, j, f)
    }

    override fun initItems() {
        super.initItems()
        height = max(height, minHeight)
    }
}