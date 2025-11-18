package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.npc.NPCClass
import com.cobblemon.mod.common.api.spawning.detail.NPCSpawnDetail
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.RegistryFriendlyByteBuf

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

    override fun containsResult(objects: Collection<*>) = false

    override fun getColor() = 0
}