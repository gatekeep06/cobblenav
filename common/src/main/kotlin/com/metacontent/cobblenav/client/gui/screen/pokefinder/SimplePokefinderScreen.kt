package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.pokefinder.FilterEntryWidget
import com.metacontent.cobblenav.client.settings.pokefinder.RadarFilterTypeRegistry
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

class SimplePokefinderScreen : AbstractModePokefinderScreen() {
    private lateinit var filterTable: TableView<FilterEntryWidget>

    override fun populateBaseTable(consumer: (AbstractWidget) -> Unit) {
        filterTable = TableView(
            x = 0,
            y = 0,
            width = 238,
            columns = 1,
            verticalGap = 6f,
            horizontalGap = 0f
        )
        populateFilterTable()
        consumer(filterTable)
    }

    override fun clearFilters() {
        filterTable.applyToAll { it.filter.clear() }
        filterTable.clear()
        populateFilterTable()
    }

    fun populateFilterTable() {
        val entries = settings?.getFilters()?.mapNotNull {
            RadarFilterTypeRegistry.get(it)?.let { type -> createEntry(type, it) }
        } ?: emptyList()
        filterTable.add(entries)
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