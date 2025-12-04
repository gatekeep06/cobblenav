package com.metacontent.cobblenav.client.gui.widget.stateful

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

abstract class StatefulWidget(
    val parentScreen: PokenavScreen? = null,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    component: Component
) : SoundlessWidget(x, y, width, height, component) {
    protected val commonChildren = mutableListOf<AbstractWidget>()
    protected abstract var state: WidgetState<*>

    open fun changeState(state: WidgetState<*>) {
        removeWidget(this.state)
        this.state = initState(state)
    }

    open fun initState(state: WidgetState<*>): WidgetState<*> {
        addWidget(state)
        parentScreen?.blockWidgets = state.blockScreenWidgets
        return state
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        state.render(guiGraphics, i, j, f)
    }

    fun addCommonWidget(widget: AbstractWidget) {
        commonChildren.add(widget)
    }

    fun removeCommonWidget(widget: AbstractWidget) {
        commonChildren.remove(widget)
    }

    override fun setX(i: Int) {
        val delta = x - i
        super.setX(i)
        state.x -= delta
    }

    override fun setY(i: Int) {
        val delta = y - i
        super.setY(i)
        state.y -= delta
    }
}