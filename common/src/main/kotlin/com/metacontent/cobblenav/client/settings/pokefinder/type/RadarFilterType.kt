package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.widget.pokefinder.FilterListEntryWidget
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.resources.ResourceLocation

interface RadarFilterType<T : RadarFilter> {
    val filterClass: Class<T>

    val typeIcon: ResourceLocation

    fun createFilter(): T

    fun createWidget(filter: T): AbstractWidget

    fun <T : RadarFilter> RadarFilterType<T>.createEntry(
        parent: PokefinderScreen,
        filter: RadarFilter? = null,
    ): FilterListEntryWidget {
        val filter = filter
            ?.takeIf { this.filterClass.isInstance(it) }
            ?.let { this.filterClass.cast(filter) } ?: this.createFilter()
        val widget = this.createWidget(filter)
        return FilterListEntryWidget(
            filter = filter,
            widget = widget,
            icon = typeIcon,
            parent = parent
        )
    }
}