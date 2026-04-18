package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.metacontent.cobblenav.client.gui.widget.pokefinder.FilterEntryWidget
import com.metacontent.cobblenav.client.settings.pokefinder.RadarFilterTypeRegistry
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

class SimplePokefinderScreen : AbstractModePokefinderScreen() {
    override fun populateBaseTable(consumer: (AbstractWidget) -> Unit) {
        settings?.getFilters()?.forEach {
            RadarFilterTypeRegistry.get(it)?.let { type -> consumer(createEntry(type, it)) }
        }
    }

    override fun clearFilters() {
        settings?.getFilters()?.forEach(RadarFilter::clear)
    }

    override fun checkBottomText(): Component? {
        return Component.empty()
    }

    override fun <T : RadarFilter> createEntry(
        type: RadarFilterType<T>,
        filter: T
    ): FilterEntryWidget {
        val widget = type.createWidget(filter)
        return FilterEntryWidget(
            filter = filter,
            widget = widget,
            icon = type.typeIcon
        )
    }
}