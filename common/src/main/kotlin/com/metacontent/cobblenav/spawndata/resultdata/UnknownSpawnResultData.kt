package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataWidget
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import org.joml.Vector3f

class UnknownSpawnResultData(
    val positionType: String
) : SpawnResultData {
    companion object {
        const val TYPE = "unknown"
        val UNKNOWN = cobblemonResource("textures/gui/pokedex/platform_unknown.png")

        fun transform(detail: SpawnDetail, player: ServerPlayer): UnknownSpawnResultData {
            return UnknownSpawnResultData(detail.spawnablePositionType.name)
        }

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): UnknownSpawnResultData = UnknownSpawnResultData(buffer.readString())
    }

    override val type = TYPE

    override val dataWidgets: List<AbstractWidget>? = null

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        val width = SpawnDataWidget.MODEL_HEIGHT - 16
        blitk(
            matrixStack = poseStack,
            texture = UNKNOWN,
            x = x - width / 2,
            y = y + 7,
            width = width,
            height = width,
            red = 0.7,
            green = 1,
            blue = 0.9
        )
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(positionType)
    }

    override fun canBeTracked() = false

    override fun containsResult(objects: Collection<*>) = false

    override fun getColor() = 0x815989

    override fun getResultName(): MutableComponent = Component.translatable("gui.cobblenav.spawn_data.unknown_pokemon")

    override fun shouldRenderPlatform() = positionType != "fishing"

    override fun shouldRenderPokeBall() = false

    override fun getRotation() = Vector3f(13F, 35F, 0F)

    override fun isUnknown() = true
}