package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.settings.pokefinder.filter.AspectFilter
import net.minecraft.network.chat.Component

object AspectFilterType : EditableTextFilterType<AspectFilter>() {
    override val filterClass = AspectFilter::class.java

    override val typeIcon = gui("pokefinder/aspect")

    override val displayedName: Component = Component.translatable("gui.cobblenav.pokefinder.filter.aspect")

    override fun createFilter(): AspectFilter = AspectFilter()
}