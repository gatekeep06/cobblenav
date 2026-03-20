package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.settings.pokefinder.filter.PokemonPropertiesFilter

object PokemonPropertiesFilterType : EditableTextFilterType<PokemonPropertiesFilter>() {
    override val filterClass = PokemonPropertiesFilter::class.java

    override val typeIcon = gui("pokefinder/pokemon_properties")

    override fun createFilter(): PokemonPropertiesFilter = PokemonPropertiesFilter()
}