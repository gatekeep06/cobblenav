package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.util.createAndGetAsRenderable
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.RegistryFriendlyByteBuf

class PokemonSpawnResultData(
    val pokemon: RenderablePokemon,
    val originalProperties: PokemonProperties
) : SpawnResultData {
    companion object {
        fun transform(detail: SpawnDetail): PokemonSpawnResultData? {
            if (detail !is PokemonSpawnDetail) {
                Cobblenav.LOGGER.error("The provided SpawnDetail type (${detail.type}) does not match the key under which it is registered (${PokemonSpawnDetail.TYPE}).")
                return null
            }

            val renderablePokemon = detail.pokemon.createAndGetAsRenderable()
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
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        pokemon.saveToBuffer(buffer)
        buffer.writeString(originalProperties.asString())
    }

    override fun canBeTracked() = true
}