package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.npc.NPCClass
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.RegistryFriendlyByteBuf

interface SpawnResultData : Encodable {
    companion object {
        val decoders: Map<String, (RegistryFriendlyByteBuf) -> SpawnResultData> = mutableMapOf(
            PokemonSpawnResultData.TYPE to PokemonSpawnResultData::decodeResultData,
            PokemonHerdSpawnResultData.TYPE to PokemonHerdSpawnResultData::decodeResultData,
            NPCSpawnResultData.TYPE to NPCSpawnResultData::decodeResultData
        )

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
        const val TYPE = "pokemon"

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): PokemonSpawnResultData = PokemonSpawnResultData(
            pokemon = RenderablePokemon.loadFromBuffer(buffer),
            originalProperties = PokemonProperties.parse(buffer.readString())
        )
    }

    override val type = TYPE

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
    val leader: RenderablePokemon,
    val leftPokemon: RenderablePokemon?,
    val rightPokemon: RenderablePokemon?
) : SpawnResultData {
    companion object {
        const val TYPE = "pokemon-herd"

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): PokemonHerdSpawnResultData = PokemonHerdSpawnResultData(
            leader = RenderablePokemon.loadFromBuffer(buffer),
            leftPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) },
            rightPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) }
        )
    }

    override val type = TYPE

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {

    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        leader.saveToBuffer(buffer)
        buffer.writeNullable(leftPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
        buffer.writeNullable(rightPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
    }

    override fun canBeTracked() = false
}

class NPCSpawnResultData(
    val npc: NPCClass
) : SpawnResultData {
    companion object {
        const val TYPE = "npc"

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): NPCSpawnResultData = NPCSpawnResultData(
            npc = NPCClass().also { it.decode(buffer) }
        )
    }

    override val type = TYPE
    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        TODO("Not yet implemented")
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        npc.encode(buffer)
    }

    override fun canBeTracked() = true
}