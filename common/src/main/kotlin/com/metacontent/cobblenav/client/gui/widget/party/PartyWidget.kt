package com.metacontent.cobblenav.client.gui.widget.party

import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

class PartyWidget(
    playerX: Int,
    playerY: Int,
    private val scale: Float,
    val player: Player? = Minecraft.getInstance().player,
    val pokemon: List<Pokemon?> = CobblemonClient.storage.myParty.slots
) : SoundlessWidget(playerX, playerY, 0, 0, Component.literal("Party Widget")
) {
    companion object {
        const val POKEMON_PLAYER_OFFSET: Int = 20
        const val POKEMON_OFFSET: Int = 15
    }

    private val memberWidgets = mutableListOf<PartyMemberWidget>()

    init {
        pokemon.forEachIndexed { index, pokemon ->
            pokemon ?: return@forEachIndexed
            val multiplier = if (index % 2 == 0) 1 else -1
            memberWidgets.add(
                PartyMemberWidget(
                    x = playerX + multiplier * (POKEMON_PLAYER_OFFSET + index * POKEMON_OFFSET),
                    y = playerY,
                    pokemon = pokemon.asRenderablePokemon(),
                    scale = scale,
                    rotationY = 0f
                )
            )
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        memberWidgets.forEach { it.render(guiGraphics, i, j, f) }
    }
}