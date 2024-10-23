package com.metacontent.cobblenav.client.gui.widget

import net.minecraft.client.gui.components.AbstractWidget

abstract class ContainerWidget<T : AbstractWidget>(
    val child: T
) : ClickableParentWidget(child.x, child.y, child.width, child.height, child.message) {
    init {
        addWidget(child)
    }

    override fun getX(): Int = child.x

    override fun setX(i: Int) {
        child.x = i
    }

    override fun getY(): Int = child.y

    override fun setY(i: Int) {
        child.y = i
    }

    override fun getWidth(): Int = child.width

    override fun setWidth(i: Int) {
        child.width = i
    }

    override fun getHeight(): Int = child.height

    override fun setHeight(i: Int) {
        child.height = i
    }
}