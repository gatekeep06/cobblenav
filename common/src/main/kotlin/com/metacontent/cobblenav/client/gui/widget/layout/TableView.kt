package com.metacontent.cobblenav.client.gui.widget.layout

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.util.Sorting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import kotlin.math.ceil
import kotlin.math.max

open class TableView<I : AbstractWidget>(
    x: Int, y: Int,
    width: Int,
    val columns: Int,
    val columnWidth: Int = width / columns,
    private val verticalGap: Float = 0f,
    private val horizontalGap: Float = (width - columns * columnWidth) / (columns - 1f),
) : SoundlessWidget(x, y, width, 0, Component.literal("Table View")) {
    internal var items = mutableListOf<I>()
    val rows
        get() = ceil(items.size.toFloat() / columns.toFloat()).toInt()

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        calculateItems()
        items.forEach { it.render(guiGraphics, i, j, f) }
//        guiGraphics.renderOutline(x, y, width, height, FastColor.ARGB32.color(255, 0, 0, 0))
    }

    fun add(widget: I) {
        items.add(widget)
    }

    fun add(widgets: List<I>) {
        items.addAll(widgets)
    }

    fun clear() {
        items.clear()
    }

    fun <T : Comparable<T>> resort(sorting: Sorting, extractor: (I) -> T) {
        val resortedItems = items.sortedWith { firstWidget, secondWidget ->
            compareValues(extractor.invoke(firstWidget), extractor.invoke(secondWidget)) * sorting.multiplier
        }
        items.clear()
        add(resortedItems)
    }

    fun applyToAll(consumer: (I) -> Unit) {
        items.forEach(consumer)
    }

    fun isEmpty() = items.isEmpty()

    open fun calculateItems() {
        val contentWidth = columns * columnWidth + (columns - 1) * horizontalGap
        val padding = (width - contentWidth) / 2
        var calculatedHeight = 0
        for (i in 0 until rows) {
            var rowHeight = 0
            for (j in 0 until columns) {
                val index = i * columns + j
                if (items.size <= index) break
                val item = items[index]
                item.x = (padding + x + j * (columnWidth + horizontalGap)).toInt()
                item.y = y + calculatedHeight
                rowHeight = max(rowHeight, item.height)
            }
            calculatedHeight += (rowHeight + verticalGap).toInt()
        }
        height = calculatedHeight
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (!clicked(pMouseX, pMouseY)) return false
        return items.any { it.mouseClicked(pMouseX, pMouseY, pButton) }
    }
}