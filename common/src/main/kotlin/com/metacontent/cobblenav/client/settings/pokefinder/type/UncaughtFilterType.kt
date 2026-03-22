package com.metacontent.cobblenav.client.settings.pokefinder.type

import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.pokefinder.UncaughtFilterWidget
import com.metacontent.cobblenav.client.settings.pokefinder.filter.UncaughtFilter
import net.minecraft.client.gui.components.AbstractWidget

object UncaughtFilterType : RadarFilterType<UncaughtFilter> {
    override val filterClass = UncaughtFilter::class.java

    override val typeIcon = gui("pokefinder/uncaught")

    override fun createFilter(): UncaughtFilter = UncaughtFilter()

    override fun createWidget(filter: UncaughtFilter): AbstractWidget {
        return UncaughtFilterWidget()
    }
}