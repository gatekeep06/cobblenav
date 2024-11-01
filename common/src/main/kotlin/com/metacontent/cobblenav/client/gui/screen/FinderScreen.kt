package com.metacontent.cobblenav.client.gui.screen

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.render.drawScaledTextJustifiedRight
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.button.TextButton
import com.metacontent.cobblenav.client.gui.widget.finder.FoundPokemonWidget
import com.metacontent.cobblenav.networking.packet.server.FindPokemonPacket
import com.metacontent.cobblenav.util.finder.FoundPokemon
import com.metacontent.cobblenav.util.SpawnData
import com.metacontent.cobblenav.util.cobblenavResource
import com.metacontent.cobblenav.util.log
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class FinderScreen(
    private val spawnData: SpawnData,
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean= false
) : PokenavScreen(makeOpeningSound, animateOpening, Component.literal("Finder")) {
    companion object {
        const val CLOSING_DURATION: Float = 3f
        const val FADING_DURATION: Float = 5f
        const val POKEBALL_PART_WIDTH: Int = 308
        const val POKEBALL_PART_HEIGHT: Int = 134
        const val FIND_BUTTON_WIDTH: Int = 112
        const val FIND_BUTTON_HEIGHT: Int = 33
        const val FIND_BUTTON_OFFSET: Int = 2
        const val FIND_BUTTON_TEXT: String = "gui.cobblenav.finder.find_button"
        val POKEBALL_TOP = cobblenavResource("textures/gui/pokeball_screen_top.png")
        val POKEBALL_BOTTOM = cobblenavResource("textures/gui/pokeball_screen_bottom.png")
        val FIND_BUTTON = cobblenavResource("textures/gui/button/find_button.png")
    }

    override val color = FastColor.ARGB32.color(255, 190, 72, 72)
    private var loading = false
    private lateinit var pokemon: FoundPokemon
    private lateinit var foundPokemonWidget: FoundPokemonWidget
    private lateinit var findButton: TextButton
    private val closingTimer = Timer(CLOSING_DURATION)
    private val fadingTimer = Timer(FADING_DURATION)
    private var pokemonX = 0
    private var pokemonY = 0

    override fun initScreen() {
        pokemonX = screenX + WIDTH / 2
        pokemonY = screenY + HEIGHT / 2

        findPokemon()

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { changeScreen(previousScreen ?: LocationScreen()) }
        ).also { addBlockableWidget(it) }
    }

    fun receiveFoundPokemon(pokemon: FoundPokemon) {
        this.pokemon = pokemon
        spawnData.pokemon.aspects += pokemon.aspects

        foundPokemonWidget = FoundPokemonWidget(pokemonX, pokemonY, spawnData, pokemon).also { addBlockableWidget(it) }

        findButton = TextButton(
            pX = screenX + (WIDTH - FIND_BUTTON_WIDTH) / 2,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - FIND_BUTTON_HEIGHT - FIND_BUTTON_OFFSET,
            pWidth = FIND_BUTTON_WIDTH,
            pHeight = FIND_BUTTON_HEIGHT,
            disabled = !pokemon.found,
            texture = FIND_BUTTON,
            text = Component.translatable(FIND_BUTTON_TEXT),
            shadow = true,
            action = {  }
        ).also { addBlockableWidget(it) }

        loading = false
    }

    override fun renderOnBackLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (loading) return

        if (!pokemon.found) {
            return
        }

        drawScaledTextJustifiedRight(
            context = guiGraphics,
            text = Component.literal(pokemon.rating.toString()),
            x = screenX + WIDTH - VERTICAL_BORDER_DEPTH - 1,
            y = screenY + HORIZONTAL_BORDER_DEPTH + 1,
            shadow = true
        )
    }

    override fun renderOnFrontLayer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

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
        val pokemon = spawnData.pokemon
        FindPokemonPacket(pokemon.species.name, pokemon.aspects).sendToServer()
    }
}