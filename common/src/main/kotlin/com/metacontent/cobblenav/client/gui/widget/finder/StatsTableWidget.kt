package com.metacontent.cobblenav.client.gui.widget.finder

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import com.metacontent.cobblenav.client.gui.util.drawBlurredArea
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.widget.button.InfoButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.finder.FoundPokemon
import com.metacontent.cobblenav.util.finder.PokemonFinder
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class StatsTableWidget(
    x: Int, y: Int,
    val spawnData: SpawnData,
    val pokemon: FoundPokemon,
    val parent: PokenavScreen
) : SoundlessWidget(x, y, WIDTH, HEIGHT, Component.literal("Stats Table")) {
    companion object {
        const val WIDTH = 82
        const val HEIGHT = 66
        const val TOP_MARGIN = 1
        const val BOTTOM_MARGIN = 2
        const val VERTICAL_MARGIN = 1
        const val ROW_HEIGHT = 15
        const val ICON_WIDTH = ROW_HEIGHT
        const val PADDING = 1f
        const val TEXT_WIDTH = 60
        const val TEXT_VERTICAL_OFFSET = 4
        const val TEXT_HORIZONTAL_OFFSET = 4
        const val TEXT_SCALE = 0.75f
        const val NOTIFICATION_WIDTH = 7
        const val NOTIFICATION_HEIGHT = 8
        const val NOTIFICATION_OFFSET = 1
        val TABLE = gui("finder/stats_table")
        val NAME = gui("finder/name")
        val TYPE = gui("finder/type")
        val ABILITY = gui("finder/ability")
        val EGG_MOVE = gui("finder/egg_move")
        val NOTIFICATION = gui("finder/notification")
    }

    private val iconTable = TableView<InfoButton>(
        x = x + VERTICAL_MARGIN,
        y = y + TOP_MARGIN,
        width = ICON_WIDTH,
        columns = 1,
        verticalGap = PADDING,
        horizontalGap = 0f
    ).also {
        it.add(
            listOf(
                InfoButton(
                    0, 0,
                    ICON_WIDTH,
                    ROW_HEIGHT,
                    Component.translatable("gui.cobblenav.info_button.header.name"),
                    Component.translatable("gui.cobblenav.info_button.body.name"),
                    texture = NAME,
                    parent = parent
                ),
                InfoButton(
                    0, 0,
                    ICON_WIDTH,
                    ROW_HEIGHT,
                    Component.translatable("gui.cobblenav.info_button.header.type"),
                    Component.translatable("gui.cobblenav.info_button.body.type"),
                    texture = TYPE,
                    parent = parent
                ),
                InfoButton(
                    0, 0,
                    ICON_WIDTH,
                    ROW_HEIGHT,
                    Component.translatable("gui.cobblenav.info_button.header.ability"),
                    Component.translatable("gui.cobblenav.info_button.body.ability"),
                    texture = ABILITY,
                    parent = parent
                ),
                InfoButton(
                    0, 0,
                    ICON_WIDTH,
                    ROW_HEIGHT,
                    Component.translatable("gui.cobblenav.info_button.header.egg_move"),
                    Component.translatable("gui.cobblenav.info_button.body.egg_move"),
                    texture = EGG_MOVE,
                    parent = parent
                )
            )
        )
        addWidget(it)
    }

    private val textX = x + 2 * VERTICAL_MARGIN + ICON_WIDTH + TEXT_HORIZONTAL_OFFSET
    private val textBaseY = y + TOP_MARGIN

//    private val nameText = spawnData.renderable.species.translatedName
//        .append(", ")
//        .append(Component.translatable("gui.cobblenav.level", pokemon.level))
//    private val typeText = Component.empty().let {
//        val primaryType = spawnData.renderable.form.primaryType
//        val secondaryType = spawnData.renderable.form.secondaryType
//        it.append(primaryType.displayName.withColor(primaryType.hue))
//        secondaryType?.let { type -> it.append(", ").append(type.displayName.withColor(type.hue)) }
//        return@let it
//    }
    private val abilityText = pokemon.ability.copy()
    private val eggMoveText = pokemon.eggMove.copy()

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()

        guiGraphics.drawBlurredArea(
            x1 = x + VERTICAL_MARGIN,
            y1 = y + TOP_MARGIN - 1,
            x2 = x + width - VERTICAL_MARGIN,
            y2 = y + height - BOTTOM_MARGIN + 1,
            blur = 3f,
            delta = f
        )
        blitk(
            matrixStack = poseStack,
            texture = TABLE,
            x = x,
            y = y,
            width = width,
            height = height
        )

//        drawScaledText(
//            context = guiGraphics,
//            text = nameText,
//            x = textX,
//            y = textBaseY + TEXT_VERTICAL_OFFSET / TEXT_SCALE,
//            maxCharacterWidth = (TEXT_WIDTH / TEXT_SCALE).toInt(),
//            scale = TEXT_SCALE
//        )
//        drawScaledText(
//            context = guiGraphics,
//            text = typeText,
//            x = textX,
//            y = textBaseY + ROW_HEIGHT + PADDING + TEXT_VERTICAL_OFFSET / TEXT_SCALE,
//            maxCharacterWidth = (TEXT_WIDTH / TEXT_SCALE).toInt(),
//            scale = TEXT_SCALE
//        )
        drawScaledText(
            context = guiGraphics,
            text = abilityText,
            x = textX,
            y = textBaseY + 2 * (ROW_HEIGHT + PADDING) + TEXT_VERTICAL_OFFSET / TEXT_SCALE,
            maxCharacterWidth = (TEXT_WIDTH / TEXT_SCALE).toInt(),
            scale = TEXT_SCALE
        )
        drawScaledText(
            context = guiGraphics,
            text = eggMoveText,
            x = textX,
            y = textBaseY + 3 * (ROW_HEIGHT + PADDING) + TEXT_VERTICAL_OFFSET / TEXT_SCALE,
            maxCharacterWidth = (TEXT_WIDTH / TEXT_SCALE).toInt(),
            scale = TEXT_SCALE
        )

        iconTable.render(guiGraphics, i, j, f)

        if (pokemon.isAbilityHidden) renderNotification(poseStack, 2)
        if (pokemon.eggMove != PokemonFinder.NO_EGG_MOVE) renderNotification(poseStack, 3)
    }

    private fun renderNotification(poseStack: PoseStack, row: Int) {
        blitk(
            matrixStack = poseStack,
            texture = NOTIFICATION,
            x = iconTable.x + iconTable.width - NOTIFICATION_WIDTH - NOTIFICATION_OFFSET,
            y = iconTable.y + row * (ROW_HEIGHT + PADDING) + NOTIFICATION_OFFSET,
            width = NOTIFICATION_WIDTH,
            height = NOTIFICATION_HEIGHT
        )
    }
}