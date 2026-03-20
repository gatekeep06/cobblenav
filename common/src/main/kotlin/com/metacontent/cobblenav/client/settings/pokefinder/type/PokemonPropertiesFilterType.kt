package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget
import com.metacontent.cobblenav.client.settings.pokefinder.filter.EditableTextFilter
import com.metacontent.cobblenav.client.settings.pokefinder.filter.PokemonPropertiesFilter
import net.minecraft.client.gui.components.AbstractWidget

object PokemonPropertiesFilterType : RadarFilterType<PokemonPropertiesFilter> {
    const val WIDGET_WIDTH = 196
    const val WIDGET_HEIGHT = 26
    const val LINE_WIDTH = 185
    const val LINE_HEIGHT = 26

    override val filterClass = PokemonPropertiesFilter::class.java

    override val typeIcon = gui("pokefinder/pokemon_properties")

    override fun createFilter(): PokemonPropertiesFilter = PokemonPropertiesFilter()

    override fun createWidget(filter: PokemonPropertiesFilter): AbstractWidget {
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
            textureSheet = EditableTextFilter.FIELD,
            onChange = filter::update
        )
    }
}