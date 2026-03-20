package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.settings.pokefinder.filter.TranslatedNameFilter

object TranslatedNameFilterType : EditableTextFilterType<TranslatedNameFilter>() {
    override val filterClass = TranslatedNameFilter::class.java

    override val typeIcon = gui("pokefinder/name")

    override fun createFilter(): TranslatedNameFilter = TranslatedNameFilter()
}