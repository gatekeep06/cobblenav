package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.settings.pokefinder.filter.LabelFilter
import net.minecraft.network.chat.Component

object LabelFilterType : EditableTextFilterType<LabelFilter>() {
    override val filterClass = LabelFilter::class.java

    override val typeIcon = gui("pokefinder/label")

    override val displayedName: Component = Component.translatable("gui.cobblenav.pokefinder.filter.label")

    override fun createFilter(): LabelFilter = LabelFilter()
}