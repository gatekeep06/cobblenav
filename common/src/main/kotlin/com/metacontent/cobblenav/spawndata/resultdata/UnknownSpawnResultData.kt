package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class UnknownSpawnResultData : SpawnResultData {
    companion object {
        const val TYPE = "unknown-pokemon"

        fun transform(detail: SpawnDetail): UnknownSpawnResultData {
            return UnknownSpawnResultData()
        }

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): UnknownSpawnResultData = UnknownSpawnResultData()
    }

    override val type = TYPE

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {

    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {}

    override fun canBeTracked() = false

    override fun containsResult(objects: Collection<*>) = false

    override fun getColor() = 0x815989

    override fun getResultName(): MutableComponent = Component.translatable("gui.cobblenav.spawn_data.unknown_pokemon")
}