package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.widget.ContextMenuWidget
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.button.TextButton
import com.metacontent.cobblenav.client.gui.widget.finder.FoundPokemonWidget
import com.metacontent.cobblenav.client.gui.widget.finder.StatsTableWidget
import com.metacontent.cobblenav.networking.packet.server.FindPokemonPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.util.finder.FoundPokemon
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class FinderScreen(
    private val spawnData: SpawnData,
    os: PokenavOS,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean= false
) : PokenavScreen(os, makeOpeningSound, animateOpening, Component.literal("Finder")) {
    companion object {
        const val CLOSING_DURATION: Float = 3f
        const val FADING_DURATION: Float = 5f
        const val POKEBALL_PART_WIDTH: Int = 308
        const val POKEBALL_PART_HEIGHT: Int = 134
        const val FIND_BUTTON_WIDTH: Int = 112
        const val FIND_BUTTON_HEIGHT: Int = 33
        const val FIND_BUTTON_OFFSET: Int = 2
        const val TABLE_OFFEST: Int = 5
        const val BUTTON_SPACE: Int = 5
        const val BUTTON_WIDTH: Int = 15
        const val BUTTON_HEIGHT: Int = 16
        const val STAR_SIZE: Int = 24
        const val STAR_OFFSET: Int = 10
        const val STAR_GAP: Int = 10
        const val FIND_BUTTON_TEXT: String = "gui.cobblenav.finder.find_button"
        val POKEBALL_TOP = cobblenavResource("textures/gui/finder/pokeball_screen_top.png")
        val POKEBALL_BOTTOM = cobblenavResource("textures/gui/finder/pokeball_screen_bottom.png")
        val DECORATIONS_0 = cobblenavResource("textures/gui/finder/finder_decorations_0.png")
        val FIND_BUTTON = cobblenavResource("textures/gui/button/find_button.png")
        val POKEFINDER = cobblenavResource("textures/gui/button/pokefinder_button.png")
        val STAR = cobblenavResource("textures/gui/finder/potential_star.png")
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
        tableX = screenX + WIDTH - VERTICAL_BORDER_DEPTH - StatsTableWidget.WIDTH - TABLE_OFFEST
        tableY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - StatsTableWidget.HEIGHT - TABLE_OFFEST

        findPokemon()

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { changeScreen(previousScreen ?: LocationScreen(os)) }
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
        spawnData.renderable.aspects += pokemon.aspects

        if (pokemon.found) {
            foundPokemonWidget = FoundPokemonWidget(pokemonX, pokemonY, spawnData, pokemon).also { addBlockableWidget(it) }
            statsTableWidget = StatsTableWidget(tableX, tableY, spawnData, pokemon, this).also { addBlockableWidget(it) }
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
        val name = spawnData.renderable.species.name
        pokefinderButton = IconButton(
            pX = findButton.x - BUTTON_SPACE - BUTTON_WIDTH,
            pY = findButton.y + (findButton.height - BUTTON_HEIGHT) / 2,
            pWidth = BUTTON_WIDTH,
            pHeight = BUTTON_HEIGHT,
            texture = POKEFINDER,
            disabled = settings?.species?.contains(name.lowercase()) == true && settings.aspects == spawnData.spawnAspects,
            action = { button ->
                settings?.let {
                    it.species += name
                    it.aspects += spawnData.spawnAspects
                    button.disabled = true
                    notifications.add(Component.translatable("gui.cobblenav.notification.pokefinder_updated"))
                    return@IconButton
                }
            }
        ).also { addBlockableWidget(it) }

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
        }
        else {
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

        guiGraphics.enableScissor(
            screenX + VERTICAL_BORDER_DEPTH,
            screenY + HORIZONTAL_BORDER_DEPTH,
            screenX + VERTICAL_BORDER_DEPTH + SCREEN_WIDTH,
            screenY + HORIZONTAL_BORDER_DEPTH + SCREEN_HEIGHT,
        )
        poseStack.pushPose()
        poseStack.translate(0f, 0f, 400f)
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
        poseStack.popPose()
        guiGraphics.disableScissor()
    }

    private fun findPokemon() {
        loading = true
        FindPokemonPacket(spawnData.renderable.species.name, spawnData.spawnAspects).sendToServer()
    }
}