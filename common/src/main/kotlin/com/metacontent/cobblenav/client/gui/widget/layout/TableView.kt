package com.metacontent.cobblenav.client.gui.widget.layout

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.util.Sorting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import kotlin.math.ceil

class TableView<I : AbstractWidget>(
    x: Int, y: Int,
    width: Int,
    val columns: Int,
    val columnWidth: Int = width / columns,
    private val verticalPadding: Int = 0,
    private val horizontalPadding: Int = (width - columns * columnWidth) / (columns - 1),
    val rowHeight: Int = 0,
) : SoundlessWidget(x, y, width, 0, Component.literal("Table View")) {
    private val items = mutableListOf<I>()
    val rows
        get() = ceil(items.size.toFloat() / columns.toFloat()).toInt()

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        items.forEach { it.render(guiGraphics, i, j, f) }
    }

    fun add(widget: I) {
        items.add(widget)
        addWidget(widget)
        initItems()
    }

    fun add(widgets: List<I>) {
        items.addAll(widgets)
        widgets.forEach(this::addWidget)
        initItems()
    }

    fun clear() {
        items.forEach { removeWidget(it) }
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

    private fun initItems() {
        height = (rowHeight + verticalPadding) * rows
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val index = i * columns + j
                if (items.size <= index) return
                val item = items[index]
                item.x = x + j * (columnWidth + horizontalPadding)
                item.y = y + i * (rowHeight + verticalPadding)
            }
        }
    }

    override fun setY(i: Int) {
        val delta = y - i
        super.setY(i)
        items.forEach { it.y -= delta }
    }

    override fun setX(i: Int) {
        val delta = x - i
        super.setX(i)
        items.forEach { it.x -= delta }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (isHovered()) {
            return super.mouseClicked(pMouseX, pMouseY, pButton)
        }
        return false
    }
}