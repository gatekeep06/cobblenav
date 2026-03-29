package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.npc.NPCClass
import com.cobblemon.mod.common.api.spawning.detail.NPCSpawnDetail
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.joml.Vector3f

class NPCSpawnResultData(
    val npc: NPCClass
) : SpawnResultData {
    companion object {
        fun decodeResultData(buffer: RegistryFriendlyByteBuf): NPCSpawnResultData = NPCSpawnResultData(
            npc = NPCClass().also { it.decode(buffer) }
        )
    }

    override val type = NPCSpawnDetail.TYPE

    override val dataWidgets: List<AbstractWidget>? = null

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        TODO("Not yet implemented")
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        npc.encode(buffer)
    }

    override fun canBeTracked() = true

    override fun containsResult(objects: Collection<*>) = false

    override fun getColor() = 0

    override fun getResultName(): MutableComponent = Component.empty()

    override fun shouldRenderPlatform(): Boolean {
        TODO("Not yet implemented")
    }

    override fun shouldRenderPokeBall(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRotation(): Vector3f {
        TODO("Not yet implemented")
    }

    override fun isUnknown(): Boolean {
        TODO("Not yet implemented")
    }
}