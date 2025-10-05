package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class FinderListEntryWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    var entry: PokemonProperties,
    val holder: PokefinderScreen
) : SoundlessWidget(x, y, width, height, Component.literal("Finder List Entry")) {
    companion object {
        const val DELETE_SIZE = 11
        const val GAP = 6
        val DELETE = gui("pokefinder/delete")
    }

    val textField = TextFieldWidget(
        fieldX = x,
        fieldY = y,
        width = width - DELETE_SIZE - GAP,
        height = height,
        default = entry.asString(),
        fillColor = FastColor.ARGB32.color(255, 20, 60, 61),
        outlineColor = FastColor.ARGB32.color(255, 31, 90, 91),
        focusedOutlineColor = FastColor.ARGB32.color(255, 84, 146, 147),
        onFinish = {
            holder.settigns?.finderEntries?.indexOf(entry)?.let { index ->
                entry = PokemonProperties.parse(it)
                holder.settigns.setEntry(index, entry)
            }
        }
    ).also { addWidget(it) }
    private val deleteButton = IconButton(
        pX = x + width - DELETE_SIZE,
        pY = y + (height - DELETE_SIZE) / 2,
        pWidth = DELETE_SIZE,
        pHeight = DELETE_SIZE,
        texture = DELETE,
        action = {
            holder.removeEntry(entry)
        }
    ).also { addWidget(it) }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        textField.render(guiGraphics, i, j, f)
        deleteButton.render(guiGraphics, i, j, f)
    }

    override fun setX(i: Int) {
        super.setX(i)
        textField.x = i
        deleteButton.x = i + width - DELETE_SIZE
    }

    override fun setY(i: Int) {
        super.setY(i)
        textField.y = i
        deleteButton.y = i + (height - DELETE_SIZE) / 2
    }

    override fun setFocused(bl: Boolean) {
        super.setFocused(bl)
        textField.isFocused = bl
    }
}