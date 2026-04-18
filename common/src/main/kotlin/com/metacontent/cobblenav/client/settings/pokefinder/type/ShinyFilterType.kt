package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.settings.pokefinder.filter.ShinyFilter
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component

object ShinyFilterType : RadarFilterType<ShinyFilter> {
    override val filterClass = ShinyFilter::class.java

    override val typeIcon = gui("pokefinder/shiny")

    override val displayedName: Component = Component.translatable("gui.cobblenav.pokefinder.filter.shiny")

    override fun createFilter(): ShinyFilter = ShinyFilter()

    override fun createWidget(filter: ShinyFilter): AbstractWidget {
        TODO("Not yet implemented")
    }
}