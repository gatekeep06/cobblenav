package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget
import com.metacontent.cobblenav.client.settings.pokefinder.filter.EditableTextFilter
import net.minecraft.client.gui.components.AbstractWidget

abstract class EditableTextFilterType<T : EditableTextFilter> : RadarFilterType<T> {
    companion object {
        const val WIDGET_WIDTH = 196
        const val WIDGET_HEIGHT = 26
        const val LINE_WIDTH = 185
        const val LINE_HEIGHT = 26
        val FIELD = gui("pokefinder/text")
    }

    override fun createWidget(filter: T): AbstractWidget {
        return TextFieldWidget(
            x = 0,
            y = 0,
            width = WIDGET_WIDTH,
            height = WIDGET_HEIGHT,
            lineWidth = LINE_WIDTH,
            lineHeight = LINE_HEIGHT,
            lineX = 5,
            default = filter.asString(),
            textColor = PokefinderScreen.COLOR,
            textureSheet = FIELD,
            onChange = filter::update
        )
    }
}