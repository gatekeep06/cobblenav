package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.asResource
import com.cobblemon.mod.common.util.pokedex
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.util.createAndGetAsRenderable
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class PokemonSpawnResultData(
    val pokemon: RenderablePokemon,
    val originalProperties: PokemonProperties
) : SpawnResultData {
    companion object {
        fun transform(detail: SpawnDetail, player: ServerPlayer): SpawnResultData? {
            if (detail !is PokemonSpawnDetail) {
                Cobblenav.LOGGER.error("The provided SpawnDetail type (${detail.type}) does not match the key under which it is registered (${PokemonSpawnDetail.TYPE}).")
                return null
            }

            val renderablePokemon = detail.pokemon.createAndGetAsRenderable()

            val speciesRecord = player.pokedex().getSpeciesRecord(renderablePokemon.species.resourceIdentifier) ?: return UnknownSpawnResultData()
            val knowledge = speciesRecord.getFormRecord(renderablePokemon.form.name)?.knowledge ?: return UnknownSpawnResultData()
            if (knowledge == PokedexEntryProgress.NONE) return UnknownSpawnResultData()

            return PokemonSpawnResultData(
                pokemon = renderablePokemon,
                originalProperties = detail.pokemon
            )
        }

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): PokemonSpawnResultData = PokemonSpawnResultData(
            pokemon = RenderablePokemon.loadFromBuffer(buffer),
            originalProperties = PokemonProperties.parse(buffer.readString())
        )
    }

    override val type = PokemonSpawnDetail.TYPE

    val state: PosableState by lazy { FloatingState() }

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        try {
            drawPokemon(
                poseStack = poseStack,
                pokemon = pokemon,
                x = x,
                y = y,
                z = z,
                delta = delta,
                state = state,
                obscured = false
            )
        } catch (e: Exception) {
            val message = Component.translatable(
                "gui.cobblenav.pokemon_rendering_exception",
                pokemon.species.translatedName.string,
                pokemon.species.translatedName.string
            )
            Cobblenav.LOGGER.error(message.string)
            Cobblenav.LOGGER.error(e.message)
            if (CobblenavClient.config.sendErrorMessagesToChat) {
                Minecraft.getInstance().player?.sendSystemMessage(message.red())
            }
            throw e
        }
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        pokemon.saveToBuffer(buffer)
        buffer.writeString(originalProperties.asString())
    }

    override fun canBeTracked() = true

    override fun containsResult(objects: Collection<*>) = objects.contains(pokemon.form.showdownId())

    override fun getColor() = pokemon.form.primaryType.hue

    override fun getResultName(): MutableComponent = pokemon.species.translatedName
}