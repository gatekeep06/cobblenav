package com.metacontent.cobblenav.spawndata.resultdata

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