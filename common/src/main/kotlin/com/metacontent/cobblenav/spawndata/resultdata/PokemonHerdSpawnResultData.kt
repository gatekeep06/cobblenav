package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.spawning.detail.PokemonHerdSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.random
import com.cobblemon.mod.common.util.randomNoCopy
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.createAndGetAsRenderable
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.RegistryFriendlyByteBuf

class PokemonHerdSpawnResultData(
    val leftPokemon: RenderablePokemon?,
    val middlePokemon: RenderablePokemon,
    val rightPokemon: RenderablePokemon?
) : SpawnResultData {
    companion object {
        fun transform(detail: SpawnDetail): PokemonHerdSpawnResultData? {
            if (detail !is PokemonHerdSpawnDetail) {
                Cobblenav.LOGGER.error("The provided SpawnDetail type (${detail.type}) does not match the key under which it is registered (${PokemonHerdSpawnDetail.TYPE}).")
                return null
            }

            val pokemon = detail.herdablePokemon
            val randomPokemon = (if (pokemon.size >= 3) {
                pokemon.randomNoCopy(3)
            } else if (pokemon.isNotEmpty()) {
                pokemon.random(3)
            } else {
                return null
            }).map { it.pokemon.createAndGetAsRenderable() }
            return PokemonHerdSpawnResultData(
                leftPokemon = randomPokemon.getOrNull(1),
                middlePokemon = randomPokemon[0],
                rightPokemon = randomPokemon.getOrNull(2)
            )
        }

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): PokemonHerdSpawnResultData = PokemonHerdSpawnResultData(
            middlePokemon = RenderablePokemon.loadFromBuffer(buffer),
            leftPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) },
            rightPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) }
        )
    }

    override val type = PokemonHerdSpawnDetail.TYPE

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {

    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        middlePokemon.saveToBuffer(buffer)
        buffer.writeNullable(leftPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
        buffer.writeNullable(rightPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
    }

    override fun canBeTracked() = false
}