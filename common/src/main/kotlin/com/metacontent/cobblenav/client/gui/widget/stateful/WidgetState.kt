package com.metacontent.cobblenav.client.gui.widget.stateful

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import net.minecraft.network.chat.Component

abstract class WidgetState<T: StatefulWidget>(
    protected val statefulWidget: T,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    component: Component
) : SoundlessWidget(x, y, width, height, component) {
    abstract val blockScreenWidgets: Boolean
}