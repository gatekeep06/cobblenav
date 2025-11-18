package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.PokemonHerdSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.asResource
import com.cobblemon.mod.common.util.random
import com.cobblemon.mod.common.util.randomNoCopy
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

class PokemonHerdSpawnResultData(
    val leftPokemon: RenderablePokemon?,
    val middlePokemon: RenderablePokemon,
    val rightPokemon: RenderablePokemon?,
    val allPokemon: List<PokemonProperties>
) : SpawnResultData {
    companion object {
        fun transform(detail: SpawnDetail): PokemonHerdSpawnResultData? {
            if (detail !is PokemonHerdSpawnDetail) {
                Cobblenav.LOGGER.error("The provided SpawnDetail type (${detail.type}) does not match the key under which it is registered (${PokemonHerdSpawnDetail.TYPE}).")
                return null
            }

            val pokemon = detail.herdablePokemon.map { it.pokemon }
            val randomPokemon = (if (pokemon.size >= 3) {
                pokemon.randomNoCopy(3)
            } else if (pokemon.isNotEmpty()) {
                pokemon.random(3)
            } else {
                return null
            }).map { it.createAndGetAsRenderable() }
            return PokemonHerdSpawnResultData(
                leftPokemon = randomPokemon.getOrNull(1),
                middlePokemon = randomPokemon[0],
                rightPokemon = randomPokemon.getOrNull(2),
                allPokemon = pokemon
            )
        }

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): PokemonHerdSpawnResultData = PokemonHerdSpawnResultData(
            middlePokemon = RenderablePokemon.loadFromBuffer(buffer),
            leftPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) },
            rightPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) },
            allPokemon = buffer.readList { PokemonProperties.parse(it.readString()) }
        )
    }

    override val type = PokemonHerdSpawnDetail.TYPE

    val state: PosableState by lazy { FloatingState() }

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        drawExamplePokemon(middlePokemon, 15f, poseStack, x, y + 3, z + 100, delta)
        leftPokemon?.let { drawExamplePokemon(it, 10f, poseStack, x - 10, y + 3, z - 100, delta) }
        rightPokemon?.let { drawExamplePokemon(it, 10f, poseStack, x + 10, y + 3, z - 100, delta) }
    }

    fun drawExamplePokemon(pokemon: RenderablePokemon, scale: Float, poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        try {
            drawPokemon(
                poseStack = poseStack,
                pokemon = pokemon,
                x = x,
                y = y,
                z = z,
                delta = delta,
                state = state,
                scale = scale,
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
        middlePokemon.saveToBuffer(buffer)
        buffer.writeNullable(leftPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
        buffer.writeNullable(rightPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
        buffer.writeCollection(allPokemon) { buf, properties -> buf.writeString(properties.asString()) }
    }

    override fun canBeTracked() = false

    override fun containsResult(objects: Collection<*>) = false

    override fun getColor() = middlePokemon.form.primaryType.hue

    override fun getResultName(): MutableComponent {
        val component = Component.translatable("gui.cobblenav.spawn_data.herd")
        allPokemon.forEach { pokemon ->
            pokemon.species?.asResource()?.let { component.append(Component.translatable("${it.namespace}.species.${it.path}.name")) }
        }
        return component
    }
}