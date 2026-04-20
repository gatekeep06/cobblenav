package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.settings.pokefinder.filter.PokemonPropertiesFilter
import net.minecraft.network.chat.Component

object PokemonPropertiesFilterType : EditableTextFilterType<PokemonPropertiesFilter>() {
    override val filterClass = PokemonPropertiesFilter::class.java

    override val typeIcon = gui("pokefinder/pokemon_properties")

    override val displayedName: Component = Component.translatable("gui.cobblenav.pokefinder.filter.properties")

    override fun createFilter(): PokemonPropertiesFilter = PokemonPropertiesFilter()
}