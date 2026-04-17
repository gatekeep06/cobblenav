package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.settings.pokefinder.filter.TranslatedNameFilter
import net.minecraft.network.chat.Component

object TranslatedNameFilterType : EditableTextFilterType<TranslatedNameFilter>() {
    override val filterClass = TranslatedNameFilter::class.java

    override val typeIcon = gui("pokefinder/name")

    override val displayedName: Component = Component.translatable("gui.cobblenav.pokefinder.filter.name")

    override fun createFilter(): TranslatedNameFilter = TranslatedNameFilter()
}