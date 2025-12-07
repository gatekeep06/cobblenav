package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.joml.Vector3f

interface SpawnResultData : Encodable {
    companion object {
        private val transformers: HashMap<String, (SpawnDetail, ServerPlayer) -> SpawnResultData?> = hashMapOf()

        private val decoders: HashMap<String, (RegistryFriendlyByteBuf) -> SpawnResultData> = hashMapOf()

        fun register(
            type: String,
            transformer: (SpawnDetail, ServerPlayer) -> SpawnResultData?,
            decoder: (RegistryFriendlyByteBuf) -> SpawnResultData
        ) {
            transformers[type] = transformer
            decoders[type] = decoder
        }

        fun fromDetail(detail: SpawnDetail, player: ServerPlayer): SpawnResultData? = transformers[detail.type]?.invoke(detail, player)

        fun decode(buffer: RegistryFriendlyByteBuf): SpawnResultData {
            val type = buffer.readString()
            return decoders[type]?.invoke(buffer) ?: throw IllegalStateException("Unknown spawn result data type")
        }
    }

    val type: String

    val dataWidgets: List<AbstractWidget>?

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

    fun containsResult(objects: Collection<*>): Boolean

    fun getColor(): Int

    fun getResultName(): MutableComponent

    fun shouldRenderPlatform(): Boolean

    fun shouldRenderPokeBall(): Boolean

    fun getRotation(): Vector3f

    fun isUnknown(): Boolean
}