package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.metacontent.cobblenav.client.gui.util.renderAdvancedTooltip
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

data class CheckedSpawnData(
    val data: SpawnData,
    val chance: Float,
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = CheckedSpawnData(
            data = SpawnData.decode(buffer),
            chance = buffer.readFloat()
        )
    }

    var chanceMultiplier = 1f

    fun renderTooltip(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int,
        lineHeight: Int = 12,
        opacity: Float = 0.9f,
        delta: Float = 0f
    ) {
        val body = mutableListOf<MutableComponent>(
            Component.translatable("gui.cobblenav.spawn_data.spawn_chance", chance * chanceMultiplier),
            Component.translatable("gui.cobblenav.spawn_data.id", data.id),
            Component.translatable("gui.cobblenav.spawn_data.position_type", data.positionType)
        )

        guiGraphics.renderAdvancedTooltip(
            header = data.result.getResultName(),
            body = body,
            mouseX = mouseX,
            mouseY = mouseY,
            x1 = x1,
            y1 = y1,
            x2 = x2,
            y2 = y2,
            lineHeight = lineHeight,
            opacity = opacity,
            headerColor = data.result.getColor() + ((opacity * 255).toInt() shl 24),
            blur = 1f,
            delta = delta
        )
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        data.encode(buffer)
        buffer.writeFloat(chance)
    }
}
