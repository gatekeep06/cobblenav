package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.pokefinder.AddFilterButton
import com.metacontent.cobblenav.client.gui.widget.pokefinder.FilterListEntryWidget
import com.metacontent.cobblenav.client.settings.pokefinder.RadarFilterTypeRegistry
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.LabelFilterType.createEntry
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

class AdvancedPokefinderScreen : AbstractModePokefinderScreen() {
    private lateinit var filterTable: TableView<FilterListEntryWidget>
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
            RadarFilterTypeRegistry.get(it.type)?.createEntry(this, it)
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

    fun createFilterOfType(type: RadarFilterType<out RadarFilter>) {
        settings ?: return
        val entry = type.createEntry(this)
        settings.addFilter(entry.filter)
        filterTable.add(entry)
    }

    fun removeFilterListEntry(entry: FilterListEntryWidget) {
        settings ?: return
        settings.removeFilter(entry.filter)
        filterTable.remove(entry)
    }
}