package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.pokefinder.AddFilterButton
import com.metacontent.cobblenav.client.gui.widget.pokefinder.AdvancedFilterEntryWidget
import com.metacontent.cobblenav.client.gui.widget.pokefinder.FilterEntryWidget
import com.metacontent.cobblenav.client.settings.pokefinder.RadarFilterTypeRegistry
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

class AdvancedPokefinderScreen : AbstractModePokefinderScreen() {
    private lateinit var filterTable: TableView<FilterEntryWidget>
    private lateinit var addButtonTable: TableView<AddFilterButton>

    override fun populateBaseTable(consumer: (AbstractWidget) -> Unit) {
        filterTable = TableView(
            x = 0,
            y = 0,
            width = 238,
            columns = 1,
            verticalGap = 6f,
            horizontalGap = 0f
        )
        settings?.getFilters()?.mapNotNull {
            RadarFilterTypeRegistry.get(it)?.let { type -> createEntry(type, it) }
        }?.let { filterTable.add(it) }

        addButtonTable = TableView(
            x = 0,
            y = 0,
            width = 238,
            columns = 5,
            verticalGap = 6f
        )
        RadarFilterTypeRegistry.advancedTypes().map { AddFilterButton(this, it) }.let {
            addButtonTable.add(it)
        }

        consumer(filterTable)
        consumer(addButtonTable)
    }

    override fun clearFilters() {
        settings?.clearFilters()
        filterTable.clear()
    }

    override fun checkBottomText(): Component? {
        var text: Component? = null
        addButtonTable.applyToAll {
            if (it.isHovered) {
                text = it.type.displayedName
                return@applyToAll
            }
        }
        return text
    }

    fun <T : RadarFilter> createFilterOfType(type: RadarFilterType<T>) {
        settings ?: return
        val filter = type.createFilter()
        val entry = createEntry(type, filter)
        settings.addFilter(entry.filter)
        filterTable.add(entry)
    }

    fun removeFilterListEntry(entry: FilterEntryWidget) {
        settings ?: return
        settings.removeFilter(entry.filter)
        filterTable.remove(entry)
    }

    override fun <T : RadarFilter> createEntry(
        type: RadarFilterType<T>,
        filter: T
    ): FilterEntryWidget {
        val widget = type.createWidget(filter)
        return AdvancedFilterEntryWidget(
            filter = filter,
            widget = widget,
            icon = type.typeIcon,
            parent = this
        )
    }
}