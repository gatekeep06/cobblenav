package com.metacontent.cobblenav.client.gui.widget.pokefinder

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.pokemon.stats.Stats.*
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.WIDGET_HEIGHT
import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen.Companion.WIDGET_WIDTH
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.CheckBox
import com.metacontent.cobblenav.client.settings.pokefinder.filter.EvYieldFilter
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.util.*

class EvYieldFilterWidget(
    val filter: EvYieldFilter
) : SoundlessWidget(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Component.empty()) {
    companion object {
        const val CHECK_BOX_WIDTH = 31
        const val CHECK_BOX_HEIGHT = 26
        val CHECK_BOX = gui("pokefinder/checkbox")

        val STATS: EnumSet<Stats> = EnumSet.of(HP, ATTACK, DEFENCE, SPECIAL_ATTACK, SPECIAL_DEFENCE, SPEED)
    }

    val stats = filter.get().toMutableSet()

    val checkBoxes = STATS.mapIndexed { index, stat ->
        stat to CheckBox(
            x = CHECK_BOX_WIDTH * index,
            y = 0,
            width = CHECK_BOX_WIDTH,
            height = CHECK_BOX_HEIGHT,
            texture = CHECK_BOX,
            default = stats.contains(stat),
            afterClick = {
                val updated = if (it.checked()) {
                    stats.add(stat)
                } else {
                    stats.remove(stat)
                }
                if (updated) filter.update(stats.toSet())
            }
        ).also { addWidget(it) }
    }.toMap()

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        checkBoxes.forEach { (stat, checkBox) ->
            checkBox.render(guiGraphics, i, j, f)

            val color = if (checkBox.checked()) PokefinderScreen.BG_COLOR else PokefinderScreen.COLOR
            drawScaledText(
                context = guiGraphics,
                text = Component.translatable("gui.cobblenav.pokefidner.ev_yield.${stat.showdownId}"),
                x = checkBox.x + checkBox.width / 2,
                y = checkBox.y + (checkBox.height - 9) / 2,
                maxCharacterWidth = checkBox.width - 4,
                colour = color,
                centered = true
            )
        }
    }

    override fun setX(i: Int) {
        checkBoxes.values.forEach { it.x += i - x }
        super.setX(i)
    }

    override fun setY(i: Int) {
        checkBoxes.values.forEach { it.y += i - y }
        super.setY(i)
    }
}