package com.metacontent.cobblenav.client.gui.screen.pokefinder

import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.overlay.PokefinderOverlay.Companion.RADIUS
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.pokefinder.AddFilterButton
import com.metacontent.cobblenav.client.gui.widget.pokefinder.FilterListEntryWidget
import com.metacontent.cobblenav.client.settings.pokefinder.RadarFilterTypeRegistry
import com.metacontent.cobblenav.client.settings.pokefinder.filter.RadarFilter
import com.metacontent.cobblenav.client.settings.pokefinder.type.LabelFilterType.createEntry
import com.metacontent.cobblenav.client.settings.pokefinder.type.RadarFilterType
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.phys.AABB

class AdvancedPokefinderScreen : PokefinderScreen() {
    private lateinit var filterTable: TableView<FilterListEntryWidget>
    private lateinit var addButtonTable: TableView<AddFilterButton>
    private lateinit var baseTable: TableView<AbstractWidget>
    private lateinit var scrollableView: ScrollableView
    private lateinit var clearButton: IconButton

    private val settings = CobblenavClient.pokefinderSettings

    private var bottomText: Component? = null

    override fun initScreen() {
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
        RadarFilterTypeRegistry.types().map { AddFilterButton(this, it) }.let {
            addButtonTable.add(it)
        }

        baseTable = TableView(
            x = screenX + BORDER_WIDTH + 36,
            y = screenY + BORDER_WIDTH + 16,
            width = 238,
            columns = 1,
            verticalGap = 0f,
            horizontalGap = 0f
        )
        baseTable.add(filterTable)
        baseTable.add(addButtonTable)

        scrollableView = ScrollableView(
            x = baseTable.x,
            y = baseTable.y,
            width = 241,
            height = 150,
            child = baseTable
        ).also { addRenderableWidget(it) }

        clearButton = IconButton(
            pX = screenX + BORDER_WIDTH + 1,
            pY = screenY + BORDER_WIDTH + 1,
            pWidth = BUTTON_SIZE,
            pHeight = BUTTON_SIZE,
            action = {
                settings?.clearFilters()
                filterTable.clear()
            },
            texture = CLEAR
        ).also { addRenderableWidget(it) }
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderBackground(guiGraphics, i, j, f)

        val pos = player?.position()
        val entityNumber = if (pos != null && settings != null) {
            minecraft?.level?.getEntitiesOfClass(
                PokemonEntity::class.java,
                AABB.ofSize(pos, RADIUS, RADIUS, RADIUS)
            ) { settings.test(it.pokemon) }?.size ?: 0
        } else {
            0
        }
        drawScaledText(
            context = guiGraphics,
            text = Component.literal(entityNumber.toString().padStart(3, '0')),
            x = screenX + BORDER_WIDTH + 14,
            y = screenY + 86 + 7,
            maxCharacterWidth = 17,
            colour = color,
            centered = true
        )

        addButtonTable.applyToAll {
            if (it.isHovered) {
                bottomText = it.type.displayedName
                return@applyToAll
            }
        }
        bottomText?.let {
            drawScaledText(
                context = guiGraphics,
                text = it as MutableComponent,
                x = screenX + BORDER_WIDTH + 87,
                y = screenY + BORDER_WIDTH + 171,
                maxCharacterWidth = 186,
                colour = color
            )
        }
        bottomText = null
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