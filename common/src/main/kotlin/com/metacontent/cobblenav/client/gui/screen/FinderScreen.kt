package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.cobblenavScissor
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.ContextMenuWidget
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.button.TextButton
import com.metacontent.cobblenav.client.gui.widget.finder.FoundPokemonWidget
import com.metacontent.cobblenav.client.gui.widget.finder.StatsTableWidget
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.finder.FoundPokemon
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import org.joml.Vector3d
import org.joml.Vector3f

class FinderScreen(
    private val spawnData: SpawnData,
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Finder")) {
    companion object {
        const val CLOSING_DURATION = 3f
        const val FADING_DURATION = 5f
        const val POKEBALL_PART_WIDTH = 308
        const val POKEBALL_PART_HEIGHT = 134
        const val FIND_BUTTON_WIDTH = 112
        const val FIND_BUTTON_HEIGHT = 33
        const val FIND_BUTTON_OFFSET = 0
        const val TABLE_OFFSET = 5
        const val BUTTON_SPACE = 5
        const val BUTTON_WIDTH = 15
        const val BUTTON_HEIGHT = 16
        const val STAR_SIZE = 24
        const val STAR_OFFSET = 10
        const val STAR_GAP = 10
        const val FIND_BUTTON_TEXT = "gui.cobblenav.finder.find_button"
        val POKEBALL_TOP = gui("finder/pokeball_screen_top")
        val POKEBALL_BOTTOM = gui("finder/pokeball_screen_bottom")
        val DECORATIONS_0 = gui("finder/finder_decorations_0")
        val FIND_BUTTON = gui("button/find_button")
        val POKEFINDER = gui("button/pokefinder_button")
        val STAR = gui("finder/potential_star")
    }

    override val color = FastColor.ARGB32.color(255, 190, 72, 72)
    private var loading = false
    private lateinit var pokemon: FoundPokemon
    private lateinit var foundPokemonWidget: FoundPokemonWidget
    private lateinit var statsTableWidget: StatsTableWidget
    private lateinit var findButton: TextButton
    private lateinit var pokefinderButton: IconButton
    private lateinit var supportContextMenu: ContextMenuWidget
    private val closingTimer = Timer(CLOSING_DURATION)
    private val fadingTimer = Timer(FADING_DURATION)
    private var pokemonX = 0
    private var pokemonY = 0
    private var tableX = 0
    private var tableY = 0

    override fun initScreen() {
        pokemonX = screenX + WIDTH / 2
        pokemonY = screenY + HEIGHT / 2
        tableX = screenX + WIDTH - VERTICAL_BORDER_DEPTH - StatsTableWidget.WIDTH - TABLE_OFFSET
        tableY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - StatsTableWidget.HEIGHT - TABLE_OFFSET

        findPokemon()

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { toPreviousScreen() }
        ).also { addBlockableWidget(it) }

        supportContextMenu = ContextMenuWidget(
            text = listOf(
                Component.translatable("gui.cobblenav.support.finder_screen"),
                Component.literal(" "),
                Component.translatable("gui.cobblenav.support.potential_stars"),
                Component.literal(" "),
                Component.translatable("gui.cobblenav.support.stats_table"),
                Component.literal(" "),
                Component.translatable("gui.cobblenav.support.track_button"),
                Component.literal(" "),
                Component.translatable("gui.cobblenav.support.pokefinder_button")
            ),
            pX = (width - ContextMenuWidget.WIDTH) / 2,
            pY = height / 2,
            lineHeight = 7,
            centerText = false,
            textWidth = ContextMenuWidget.WIDTH - 20,
            cancelAction = { menu, _ ->
                blockWidgets = false
                removeUnblockableWidget(menu)
                menu.openingTimer.reset()
            }
        )
    }

    fun receiveFoundPokemon(pokemon: FoundPokemon) {
        this.pokemon = pokemon
//        spawnData.renderable.aspects += pokemon.aspects

        if (pokemon.found) {
            foundPokemonWidget =
                FoundPokemonWidget(pokemonX, pokemonY, spawnData, pokemon).also { addBlockableWidget(it) }
            statsTableWidget =
                StatsTableWidget(tableX, tableY, spawnData, pokemon, this).also { addBlockableWidget(it) }
        } else {
            (previousScreen as? LocationScreen)?.checkNearbyPokemon()
        }

        findButton = TextButton(
            pX = screenX + (WIDTH - FIND_BUTTON_WIDTH) / 2,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - FIND_BUTTON_HEIGHT - FIND_BUTTON_OFFSET,
            pWidth = FIND_BUTTON_WIDTH,
            pHeight = FIND_BUTTON_HEIGHT,
            disabled = !pokemon.found,
            texture = FIND_BUTTON,
            text = Component.translatable(FIND_BUTTON_TEXT),
            shadow = true,
            action = {
                CobblenavClient.trackArrowOverlay.entityId = pokemon.entityId
                onClose()
            }
        ).also { addBlockableWidget(it) }

        val settings = CobblenavClient.pokefinderSettings
//        val name = spawnData.renderable.species.name
//        pokefinderButton = IconButton(
//            pX = findButton.x - BUTTON_SPACE - BUTTON_WIDTH,
//            pY = findButton.y + (findButton.height - BUTTON_HEIGHT) / 2,
//            pWidth = BUTTON_WIDTH,
//            pHeight = BUTTON_HEIGHT,
//            texture = POKEFINDER,
//            disabled = settings?.species?.contains(name.lowercase()) == true && settings.aspects == spawnData.spawnAspects,
//            action = { button ->
//                settings?.let {
//                    it.species += name
//                    it.aspects += spawnData.spawnAspects
//                    button.disabled = true
//                    notifications.add(Component.translatable("gui.cobblenav.notification.pokefinder_updated"))
//                    return@IconButton
//                }
//            }
//        ).also { addBlockableWidget(it) }

        IconButton(
            pX = pokefinderButton.x - BUTTON_SPACE - BUTTON_WIDTH,
            pY = pokefinderButton.y,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            texture = SUPPORT,
            action = {
                blockWidgets = true
                addUnblockableWidget(supportContextMenu)
            }
        ).also { addBlockableWidget(it) }

        loading = false
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()

        blitk(
            matrixStack = poseStack,
            texture = DECORATIONS_0,
            x = screenX + VERTICAL_BORDER_DEPTH,
            y = screenY + HORIZONTAL_BORDER_DEPTH,
            width = WIDTH - 2 * VERTICAL_BORDER_DEPTH,
            height = HEIGHT - 2 * HORIZONTAL_BORDER_DEPTH,
            alpha = 0.6f
        )

        if (loading) return

        if (!pokemon.found) {
            drawScaledText(
                context = guiGraphics,
                text = Component.translatable("gui.cobblenav.finder.pokemon_not_found"),
                x = width / 2,
                y = height / 2,
                centered = true,
                shadow = true
            )
            return
        }

        for (i in 0 until pokemon.potentialStars) {
            blitk(
                matrixStack = poseStack,
                texture = STAR,
                x = screenX + VERTICAL_BORDER_DEPTH + STAR_OFFSET + STAR_GAP * i,
                y = screenY + HORIZONTAL_BORDER_DEPTH + STAR_OFFSET,
                width = STAR_SIZE,
                height = STAR_SIZE,
                red = 0.4 + 0.2 * pokemon.potentialStars,
                green = 0.15 + 0.2 * pokemon.potentialStars,
                blue = 0.2
            )
        }
//        drawScaledTextJustifiedRight(
//            context = guiGraphics,
//            text = Component.literal(pokemon.rating.toString()),
//            x = screenX + WIDTH - VERTICAL_BORDER_DEPTH - 1,
//            y = screenY + HORIZONTAL_BORDER_DEPTH + 1,
//            shadow = true
//        )
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (closingTimer.isOver()) {
            super.render(guiGraphics, mouseX, mouseY, delta)
        } else {
            previousScreen?.render(guiGraphics, mouseX, mouseY, delta)
        }
        if (!fadingTimer.isOver()) {
            renderPokeballAnimation(guiGraphics, mouseX, mouseY, delta)
            closingTimer.tick(delta)
            if (!closingTimer.isOver()) return
            fadingTimer.tick(delta)
        }
    }

    private fun renderPokeballAnimation(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val poseStack = guiGraphics.pose()

        guiGraphics.cobblenavScissor(
            screenX + VERTICAL_BORDER_DEPTH,
            screenY + HORIZONTAL_BORDER_DEPTH,
            screenX + VERTICAL_BORDER_DEPTH + SCREEN_WIDTH,
            screenY + HORIZONTAL_BORDER_DEPTH + SCREEN_HEIGHT,
        )
        poseStack.pushAndPop(
            translate = Vector3d(0.0, 0.0, 400.0),
            scale = Vector3f(scale, scale, 1f)
        ) {
            blitk(
                matrixStack = poseStack,
                texture = POKEBALL_BOTTOM,
                x = screenX + VERTICAL_BORDER_DEPTH,
                y = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - closingTimer.getProgress() * POKEBALL_PART_HEIGHT,
                width = POKEBALL_PART_WIDTH,
                height = POKEBALL_PART_HEIGHT,
                alpha = 1 - fadingTimer.getProgress()
            )
            blitk(
                matrixStack = poseStack,
                texture = POKEBALL_TOP,
                x = screenX + VERTICAL_BORDER_DEPTH,
                y = screenY + HORIZONTAL_BORDER_DEPTH - POKEBALL_PART_HEIGHT + closingTimer.getProgress() * POKEBALL_PART_HEIGHT,
                width = POKEBALL_PART_WIDTH,
                height = POKEBALL_PART_HEIGHT,
                alpha = 1f - fadingTimer.getProgress()
            )
        }

        guiGraphics.disableScissor()
    }

    private fun findPokemon() {
        loading = true
//        FindPokemonPacket(spawnData.renderable.species.name, spawnData.spawnAspects).sendToServer()
    }
}