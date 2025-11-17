package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.npc.NPCClass
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.NPCSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.PokemonHerdSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.random
import com.cobblemon.mod.common.util.randomNoCopy
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.util.createAndGetAsRenderable
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.RegistryFriendlyByteBuf

interface SpawnResultData : Encodable {
    companion object {
        private val transformers: HashMap<String, (SpawnDetail) -> SpawnResultData?> = hashMapOf()

        private val decoders: HashMap<String, (RegistryFriendlyByteBuf) -> SpawnResultData> = hashMapOf()

        fun register(
            type: String,
            transformer: (SpawnDetail) -> SpawnResultData?,
            decoder: (RegistryFriendlyByteBuf) -> SpawnResultData
        ) {
            transformers[type] = transformer
            decoders[type] = decoder
        }

        fun fromDetail(detail: SpawnDetail): SpawnResultData? = transformers[detail.id]?.invoke(detail)

        fun decode(buffer: RegistryFriendlyByteBuf): SpawnResultData {
            val type = buffer.readString()
            return decoders[type]?.invoke(buffer) ?: throw IllegalStateException("Unknown spawn result data type")
        }
    }

    val type: String

    fun drawResult(
        poseStack: PoseStack,
        x: Float = 0f,
        y: Float = 0f,
        z: Float = 0f,
        delta: Float = 0f
    )

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(type)
        encodeResultData(buffer)
    }

    fun encodeResultData(buffer: RegistryFriendlyByteBuf)

    fun canBeTracked(): Boolean
}

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

class NPCSpawnResultData(
    val npc: NPCClass
) : SpawnResultData {
    companion object {
        fun decodeResultData(buffer: RegistryFriendlyByteBuf): NPCSpawnResultData = NPCSpawnResultData(
            npc = NPCClass().also { it.decode(buffer) }
        )
    }

    override val type = NPCSpawnDetail.TYPE
    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        TODO("Not yet implemented")
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        npc.encode(buffer)
    }

    override fun canBeTracked() = true
}