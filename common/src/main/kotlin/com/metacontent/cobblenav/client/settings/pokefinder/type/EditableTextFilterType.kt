package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.FIELD
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.LINE_HEIGHT
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.LINE_WIDTH
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.WIDGET_HEIGHT
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.WIDGET_WIDTH
import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget
import com.metacontent.cobblenav.client.settings.pokefinder.filter.EditableTextFilter
import net.minecraft.client.gui.components.AbstractWidget

abstract class EditableTextFilterType<T : EditableTextFilter> : RadarFilterType<T> {
    override fun createWidget(filter: T): AbstractWidget {
        return TextFieldWidget(
            x = 0,
            y = 0,
            width = WIDGET_WIDTH,
            height = WIDGET_HEIGHT,
            lineWidth = LINE_WIDTH,
            lineHeight = LINE_HEIGHT,
            lineX = 12,
            default = filter.asString(),
            textColor = PokefinderScreen.COLOR,
            textureSheet = FIELD,
            onChange = filter::update
        )
    }
}